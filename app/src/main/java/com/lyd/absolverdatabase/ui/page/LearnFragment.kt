package com.lyd.absolverdatabase.ui.page

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.MainActivity
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.state.LearnState
import com.lyd.absolverdatabase.bridge.state.LearnViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentLearnBinding
import com.lyd.absolverdatabase.ui.adapter.LearnVideoAdapter
import com.lyd.absolverdatabase.ui.adapter.SearchVideoAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.ui.widgets.BaseDialogBuilder
import com.lyd.absolverdatabase.utils.FirstUtil
import kotlinx.coroutines.flow.collectLatest

class LearnFragment : BaseFragment() {

    private var learnBinding : FragmentLearnBinding ? = null
    private val learnState : LearnState by viewModels{
        LearnViewModelFactory((mActivity?.application as App).bilibiliRepository)
    }

    private var helper: QuickAdapterHelper ?= null
    private val latestVideoAdapter : SearchVideoAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        SearchVideoAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                val uri = Uri.parse("https://www.bilibili.com/video/${adapter.getItem(position)!!.bvid}")
                val intent = Intent(Intent.ACTION_VIEW,uri)
                startActivity(intent)
            }
        }
    }
    private val learnVideoAdapter : LearnVideoAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        LearnVideoAdapter().apply {
            setOnItemClickListener{adapter, view, position ->
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.bilibili.com/video/${adapter.getItem(position)!!.bvid}")
                    )
                )
            }
        }
    }

    private val selectChoice by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        arrayOf(getString(R.string.ghost_teach),getString(R.string.latest_video))
    }
    private var checkItem :Int = 0// 默认选第一个
    private val dialog by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        BaseDialogBuilder(requireActivity())
            .setTitle(getString(R.string.choice_video))
            .setSingleChoiceItems(selectChoice, checkItem){ dialog, which ->
                request(which,isManualRefresh = true)
                dialog.dismiss()
            }
            .create()
    }

    private val pageMap = mutableMapOf("page" to "1",
        "page_size" to "20",
        "order" to "pubdate",
        "keyword" to "赦免者",
        "search_type" to "video")
    private val learnMap = mapOf("mid" to "11736402","season_id" to "23870",
        "sort_reverse" to "false","page_num" to "1","page_size" to "30")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化viewModel
//        learnState = getFragmentViewModelProvider(this)[LearnState::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载界面
        val view:View = inflater.inflate(R.layout.fragment_learn,container,false)

        // 绑定binding，设置vm，设置点击事件
        learnBinding = FragmentLearnBinding.bind(view)
        learnBinding?.vm = learnState
        learnBinding?.click = ClickProxy()
        learnBinding?.lifecycleOwner = viewLifecycleOwner // 注意，这里一定是要view的生命周期，防止被回收时还出问题

        initRecyclerAndAdapter()
        initRefreshLayout()

        checkItem = learnState.searchSelect.value


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化列表
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            Log.i(TAG, "我先拿cookie: ${learnState.getCookie()}")// 依靠协程，按序执行

            if (checkItem == 1){
                if (learnState.searchVideoSharedFlow.replayCache.isNotEmpty() &&
                    learnState.searchVideoSharedFlow.replayCache[0].isNotEmpty()){
                    Log.i(TAG, "有缓存数据，不需要进行网络请求")
                } else{
                    Log.i(TAG, "无缓存数据")
                    request(checkItem,FirstUtil.isLearnVideoSearchFirst())
                }
            } else if (checkItem == 0){
                request(checkItem,FirstUtil.isVideosSearchFirst())
            }
        }

        /**------------------------------下面是flow的监听---------------------------**/

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            learnState.searchSelect.collect(){
                checkItem = it
//                Log.i(TAG, "checkItem变化了，现在是 $checkItem")
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            learnState.searchVideoSharedFlow.collectLatest{
                Log.i(TAG, "onViewCreated: receive searchVideoSharedFlow 数量${it.size}")
//                it.forEach {video ->
//                    Log.i(TAG, "onViewCreated: ${video.title}")
//                }
                if (checkItem == 1){
                    learnBinding?.learnRecycle?.adapter = latestVideoAdapter
                    latestVideoAdapter.submitList(it)
                    learnBinding?.learnRefresh?.isRefreshing = false
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            learnState.learnVideoSharedFlow.collectLatest{
                if (checkItem == 0){
                    learnBinding?.learnRecycle?.adapter = learnVideoAdapter
                    learnVideoAdapter.submitList(it)
                    learnBinding?.learnRefresh?.isRefreshing = false
                }

            }
        }
    }

    private fun initRecyclerAndAdapter(){
        // 使用这个，给未来的加载更多做铺垫
        helper = QuickAdapterHelper.Builder(latestVideoAdapter)
            .setTrailingLoadStateAdapter(object :TrailingLoadStateAdapter.OnTrailingListener{
                override fun onFailRetry() {

                }

                override fun onLoad() {

                }

                override fun isAllowLoading(): Boolean {
                    return !learnBinding!!.learnRefresh.isRefreshing
                }
            }).build()
        learnBinding?.learnRecycle?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = learnVideoAdapter
            addOnScrollListener(object :RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    // OnScrollListener.SCROLL_STATE_FLING; //屏幕处于甩动状态
                    // OnScrollListener.SCROLL_STATE_IDLE; //停止滑动状态
                    // OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;// 手指接触状态

                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy < 0){// 当前处于上滑状态
                        // 显示navigation
                        (mActivity as MainActivity).hideOrShowBottomNav(1)

                    } else if (dy > 0){// 当前处于下滑状态
                        // 藏起navigation
                        (mActivity as MainActivity).hideOrShowBottomNav(0)
                    }
                }
            })
        }

    }

    private fun initRefreshLayout(){
        learnBinding?.learnRefresh?.setOnRefreshListener {
            lifecycleScope.launchWhenStarted {
                request(checkItem,true)
            }
        }
    }

    private fun request(searchWhich :Int = 0,isManualRefresh :Boolean = false){
        when(searchWhich){
            0 ->{
                Log.i(TAG, "request: 搜索老G视频")
                lifecycleScope.launchWhenStarted {
                    learnState.getLearnVideo(learnMap,
                        ifError = whenLoadError)
                }
            }
            1 ->{// 常规搜索赦免者
                lifecycleScope.launchWhenStarted {
                    learnState.getVideoList(pageMap,
                        ifError = whenLoadError,
                        isManualRefresh = isManualRefresh)
                }
            }
        }
    }

    private val whenLoadError :(errorMsg:String) -> Any? = {
        learnBinding?.learnRefresh?.isRefreshing = false
        showShortToast("网络请求错误:$it")
    }

    inner class ClickProxy {
        fun selectSearch(){
            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        learnBinding = null
    }
}