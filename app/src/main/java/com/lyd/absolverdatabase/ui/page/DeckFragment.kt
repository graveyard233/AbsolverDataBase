package com.lyd.absolverdatabase.ui.page

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.QuickAdapterHelper
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.Deck
import com.lyd.absolverdatabase.bridge.data.bean.DeckType
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.DeckEditState
import com.lyd.absolverdatabase.bridge.state.DeckEditViewModelFactory
import com.lyd.absolverdatabase.bridge.state.DeckState
import com.lyd.absolverdatabase.bridge.state.DeckViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentDeckBinding
import com.lyd.absolverdatabase.ui.adapter.DeckAdapter
import com.lyd.absolverdatabase.ui.adapter.DeckHeaderAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.ui.widgets.BaseDialogBuilder
import com.lyd.absolverdatabase.ui.widgets.ColorShades
import com.lyd.absolverdatabase.ui.widgets.SpacesItemDecoration
import com.lyd.absolverdatabase.utils.*
import com.lyd.architecture.utils.Utils
import kotlinx.coroutines.flow.collectLatest
import okhttp3.internal.toHexString
import java.util.Base64
import java.util.Locale

class DeckFragment :BaseFragment() {

    companion object{
        private const val sharedIdTag :Int = -2
    }

    private var deckBinding : FragmentDeckBinding? = null
    private val deckState : DeckState by navGraphViewModels(navGraphId = R.id.nav_deck, factoryProducer = {
        DeckViewModelFactory((mActivity?.application as App).deckRepository)
    })
    private val editState : DeckEditState by navGraphViewModels(navGraphId = R.id.nav_deck, factoryProducer = {
        DeckEditViewModelFactory((mActivity?.application as App).deckEditRepository)
    })


    private var lastBgColor = Color.TRANSPARENT

    private val deckAdapter : DeckAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        DeckAdapter().apply {
            addOnItemChildClickListener(R.id.item_deck_constraint){adapter, view, position ->
                Log.i(TAG, "onclick: ${getItem(position)}")
                // 前往编辑界面，注意一定要把editState的forEdit的卡组置空
                editState.fromDeckToEdit()
                nav().navigate(DeckFragmentDirections.actionDeckFragmentToDeckEditFragment(getItem(position)!!))
            }
            addOnItemChildLongClickListener(R.id.item_deck_constraint){adapter, view, position ->
//                val deckForShareText = GsonUtils.toJson(getItem(position)?.copy(deckId = sharedIdTag))

                val whatDeck :Deck = getItem(position)?.copy(deckId = sharedIdTag) ?: DeckGenerate.generateEmptyDeck().apply {
                    deckId = sharedIdTag
                    createTime = System.currentTimeMillis()
                    updateTime = System.currentTimeMillis()
                }
                if (SettingRepository.isShowSeqDetailWhenSharedDeck){// 如果要显示攻击序列内的招式名称，则要在外面额外加
                    // TODO: 在这里获取提前准备好的额外文本
                    editState
                }
                val deckForShareText = StringUtils.deck2MyJson(whatDeck)
                Log.i(TAG, "长按分享的卡组代码: $deckForShareText")
                // 将卡组数据写入剪贴板
                ClipUtil.copyText(deckForShareText)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 || Locale.getDefault().toLanguageTag().startsWith("zh")){
                    Toast.makeText(Utils.getApp(),R.string.has_copy_deck_code, Toast.LENGTH_SHORT).show()
                }
                return@addOnItemChildLongClickListener true// 返回true就不会出发onclick
            }
            addOnItemChildClickListener(R.id.item_deck_img_delete){adapter,view,position ->
                Log.i(TAG, "应该删除这个卡组: ${getItem(position)}")
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    deckState.deleteOneDeck(
                        getItem(position)!!,
                        ifSuccess = {
                            adapter.removeAt(position)
                        },
                        ifError = {
                            showShortToast(it)
                        })
                }
            }
            isEmptyViewEnable = true
            setEmptyViewLayout(requireContext(),R.layout.item_deck_empty)

            animationEnable = true
            isAnimationFirstOnly = false
            setItemAnimation(BaseQuickAdapter.AnimationType.SlideInRight)
        }
    }

    private val helper :QuickAdapterHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        QuickAdapterHelper.Builder(deckAdapter)
            .build()
    }

    private val deckHeaderAdapter :DeckHeaderAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        DeckHeaderAdapter().apply {
            setOnItemClickListener(){_,_,_ ->
                editState.fromDeckToEdit()
                nav().navigate(DeckFragmentDirections.actionDeckFragmentToDeckEditFragment(
                    DeckGenerate.generateEmptyDeck(deckType = getDeckTypeByPosition(deckState.choiceFlow.value)))
                )
            }
            setOnItemLongClickListener(object :BaseQuickAdapter.OnItemLongClickListener<Any>{
                override fun onLongClick(adapter: BaseQuickAdapter<Any, *>,
                                         view: View, position: Int): Boolean {
                    if (SettingRepository.isNeedAskBeforeImport){
                        importDeckDialog.show()
                    } else {
                        importDeck()
                    }
                    return true
                }
            })
        }
    }

    private val importDeckDialog by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        BaseDialogBuilder(requireActivity())
            .setTitle(R.string.need_to_import_deck_from_clip)
            .setPositiveButton(R.string.confirm){dialog,num ->
                importDeck()
            }
            .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 以前在这里初始化viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_deck,container,false)

        deckBinding = FragmentDeckBinding.bind(view)
        deckBinding?.vm = deckState
        deckBinding?.click = ClickProxy()
        deckBinding?.lifecycleOwner = viewLifecycleOwner


        deckBinding?.apply {

        }

        initRecycler()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 在这里进行liveData的监听
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            deckState.choiceFlow.collectLatest {position ->
                // 这里要和动画分开，因为动画在这里启动会直接崩溃
                // 因为onViewCreated的时候还没有attach到fragment，没坐标，解决方案是post启动
                // 因为可以防止重复，所以可以在这里进行颜色变化和数据请求与筛选
                Log.i(TAG, "choiceFlow collect: $position -> ${getDeckTypeByPosition(position)}")
                doColorChange(position,lastBgColor) // 暂时不要做颜色渐变，因为有时候会抽风，闪的厉害
                deckState.queryDecksByDeckType(
                    getDeckTypeByPosition(position),
                    ifEmpty = {
                        Log.w(TAG, "queryDecksByDeckType is empty")
                    },
                    ifError = {
                        Log.e(TAG, "queryDecksByDeckType error: $it")
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            // TODO: 这里的接收方式有问题，我得换一种接收方式，因为没切换一次界面，我就会收到一次
            // tmd居然解决了，通过把appcompat从1.5.1升级到1.6.1，然后给lifecycleScope前面加上viewLifecycleOwner就搞定了
            deckState.deckSharedFlow.collectLatest {
                Log.i(TAG, "receive: ${it.size}")
                deckAdapter.submitList(it)
            }
        }


    }

    private fun initRecycler(){
        deckBinding?.deckRecycle!!.apply {
            adapter = helper.adapter
            addItemDecoration(SpacesItemDecoration(requireContext(),SpacesItemDecoration.VERTICAL,0,1)
                .setParam(R.color.grey_split_line,3,30F,30F))
        }
        if (helper.beforeAdapterList.isEmpty())
            helper.addBeforeAdapter(0,deckHeaderAdapter)
    }

    inner class ClickProxy {

        // 点击只处理动画和选择，但因为选择的是stateFlow，所以可以防抖防重
        fun showBg(view: View, position: Int){
            deckState.setChoice(position)
            doRevealAnimation(view)
        }
    }

    private fun getDeckTypeByPosition(position: Int) :DeckType= when(position){
        0 ->{ DeckType.HAND }
        1 ->{ DeckType.GLOVE }
        2 ->{ DeckType.SWORD }
        else -> {DeckType.HAND}
    }

    private fun doRevealAnimation(view: View){
        val bgView :View = deckBinding!!.deckBg
        //动画开始半径和结束半径，两者相对关系可用于控制是揭露还是反揭露，也即是从无到有还是从有到无
        val startRadius:Float= 0f
        val endRadius:Float=bgView.height.toFloat()
        val location = IntArray(2)
        view.getLocationInWindow(location)// 注意这里，不能写在view attach到fragment之前
        //关键代码，构建一个揭露动画对象，注意圆形揭露动画的圆心以及开始半径和结束半径是如何计算出来的，应该很好理解，这里不做过多解释
        val animReveal = ViewAnimationUtils.createCircularReveal(bgView,
            location[0] + view.width/2,
            location[1] + view.height/2,
            startRadius,
            endRadius
        )
        animReveal.duration = 600
        animReveal.start()
    }

    private fun doColorChange(position :Int,lastColor :Int){
//        val shades = ColorShades()
        val endColor :Int = getColorByPosition(position)
//        lifecycleScope.launchWhenStarted {
//            for (i in 0..500){
//                delay(1)
//                shades.setFromColor(lastColor)
//                    .setToColor(endColor)
//                    .setShade((i.toFloat()/500F).toFloat())
//                val bgView :View = deckBinding!!.deckBg
//                bgView.setBackgroundColor(shades.generate())
//            }
//            lastBgColor = endColor
//        }
        val bgView :View = deckBinding!!.deckBg
        bgView.setBackgroundColor(endColor)
    }

    @ColorInt
    private fun getColorByPosition(position: Int) : Int{
        return when(position){
            0 ->{
                requireActivity().getResourceColor(com.google.android.material.R.attr.colorPrimaryContainer)
//                getThemeAttrColor(requireContext(),R.style.Base_Theme_AbsolverDatabase,
//                    com.google.android.material.R.attr.colorPrimaryContainer)
            }

            1 ->{
                requireActivity().getResourceColor(com.google.android.material.R.attr.colorSecondaryContainer)
//                getThemeAttrColor(requireContext(),R.style.Base_Theme_AbsolverDatabase,
//                    com.google.android.material.R.attr.colorSecondaryContainer)
            }

            2 ->{
                requireActivity().getResourceColor(com.google.android.material.R.attr.colorTertiaryContainer)
//                getThemeAttrColor(requireContext(),R.style.Base_Theme_AbsolverDatabase,
//                    com.google.android.material.R.attr.colorTertiaryContainer)
            }

            else ->{
                Color.TRANSPARENT
            }
        }
    }

    @Deprecated(message = "这样拿会只拿到默认颜色，取不到动态颜色", replaceWith = ReplaceWith("requireActivity().getResourceColor(com.google.android.material.R.attr./*color you want*/)"))
    @ColorInt
    private fun getThemeAttrColor(@NonNull context: Context, @StyleRes themeResId: Int, @AttrRes attrResId: Int): Int {
        return MaterialColors.getColor(ContextThemeWrapper(context, themeResId), attrResId, Color.WHITE)
    }

    private fun importDeck(){
        // 从剪贴板中读取数据
        Log.i(TAG, "deckHeaderAdapter onLongClick: ${ClipUtil.readText()}")
        val tempText = ClipUtil.readText()
        if (tempText == "null"){
            showShortToast(getString(R.string.clipboard_is_empty))
            return
        }
        var deckToSaved :Deck ?= null
        try {
            deckToSaved = StringUtils.myJson2Deck(tempText).apply { this.deckId = 0 }
//            deckToSaved = GsonUtils.fromJson<Deck>(ClipUtil.readText(),Deck::class.java)
//                .apply { this.deckId = 0 }
        } catch (e: Exception) {
            Log.e(TAG, "onLongClick: 错误的卡组代码数据")
            showShortToast(getString(R.string.error_deck_data))
            return
        }
        if (deckToSaved != null && deckToSaved.deckId == 0){
            Log.i(TAG, "onLongClick: 获取卡组成功")
            editState.saveDeckFromShared(
                deckToSaved,
                ifError = {
                    Log.e(TAG, "onLongClick: 保存分享卡组失败 $it")
                    showShortToast(getString(R.string.save_deck_by_shared_false,it))
                },
                ifSuccess = {
                    Log.i(TAG, "onLongClick: 保存卡组成功，id为 $it")
                    // 这里应该刷新列表
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        // 不能通过setChoice来获取，而是得手动查询才行，因为stateFlow相同的数据不会触发collect
                        deckState.queryDecksByDeckType(getDeckTypeByPosition(deckState.choiceFlow.value))
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        deckBinding = null
    }
}