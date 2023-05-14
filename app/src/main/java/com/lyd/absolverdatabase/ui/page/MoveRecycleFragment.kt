package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.MoveForSelect
import com.lyd.absolverdatabase.bridge.state.DeckEditState
import com.lyd.absolverdatabase.bridge.state.DeckEditViewModelFactory
import com.lyd.absolverdatabase.bridge.state.MoveRecycleState
import com.lyd.absolverdatabase.bridge.state.MoveRecycleViewModelFactory
import com.lyd.absolverdatabase.ui.adapter.MoveItemAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.utils.SideUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 注意，这里不要在构造函数里面放参数，除非把baseFragment的构造函数变成open，要什么东西用bundle传递就好
 * */
class MoveRecycleFragment :BaseFragment()
{

    private var whatSide :Int = 0

    override val TAG = "${javaClass.simpleName}-${javaClass.hashCode()}"

    // 由于创建了很多个Fragment，所以这个也不是唯一的
    private val moveRecycleState by viewModels<MoveRecycleState> {
        MoveRecycleViewModelFactory((mActivity?.application as App).moveRepository)
    }

    // 这个是唯一的，所以通过这个来设置筛选的选项，所以这个state用来观察筛选flow
    private val editState : DeckEditState by navGraphViewModels(navGraphId = R.id.nav_deck, factoryProducer = {
        DeckEditViewModelFactory((mActivity?.application as App).deckEditRepository)
    })


    private val moveAdapter :MoveItemAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        MoveItemAdapter().apply {
            setOnItemClickListener(object :BaseQuickAdapter.OnItemClickListener<MoveForSelect>{
                override fun onClick(
                    adapter: BaseQuickAdapter<MoveForSelect, *>,
                    view: View,
                    position: Int
                ) {
//                    Log.i(TAG, "onClick: 我点击的招式是 ${adapter.getItem(position)!!.moveOrigin}")
                    adapter.getItem(position)?.run {
                        onSelect.invoke(this)
                        editState.changeFilter()
                    }
                }
            })
        }
    }

    private val gridLayoutManager :GridLayoutManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        GridLayoutManager(requireContext(),4)
    }

    private val sideList = mutableListOf<MoveForSelect>()

    private lateinit var recycle :RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view :View = inflater.inflate(R.layout.fragment_move_recycle,container,false)

        arguments?.getInt("whatSide")?.let {
            whatSide = it
        }

        recycle = view.findViewById(R.id.moveRecycle_recycle)

        recycle.apply {
            layoutManager = gridLayoutManager
        }.apply {
            adapter = moveAdapter
        }

        Log.i(TAG, "onCreateView: side ${SideUtil.getSideByInt(whatSide)}")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                sideList.clear()
                sideList.addAll(moveRecycleState.originListByStartSide(whatSide).map { MoveForSelect(it) })
                moveAdapter.submitList(sideList)
                moveAdapter.getItem((0..moveAdapter.itemCount).random())?.apply {
                    isSelected = true
                }
                Log.i(TAG, "repeatOnLifecycle: 接收到数据并进行加载")
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                editState.filterOptionFlow.collectLatest {
                    Log.i(TAG, "接受到筛选数据: ${it.attackToward.javaClass.simpleName}")
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView:  side ${SideUtil.getSideByInt(whatSide)}")
    }

    var onSelect :(MoveForSelect) ->Unit = {}
}