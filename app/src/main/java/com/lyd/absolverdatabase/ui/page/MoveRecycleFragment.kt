package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.state.DeckEditState
import com.lyd.absolverdatabase.bridge.state.DeckEditViewModelFactory
import com.lyd.absolverdatabase.bridge.state.MoveRecycleState
import com.lyd.absolverdatabase.bridge.state.MoveRecycleViewModelFactory
import com.lyd.absolverdatabase.ui.adapter.MoveItemAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.utils.SideUtil
import kotlinx.coroutines.flow.collectLatest

/**
 * 注意，这里不要在构造函数里面放参数，除非把baseFragment的构造函数变成open，要什么东西用bundle传递就好
 * */
class MoveRecycleFragment :BaseFragment()
{

    private var whatSide :Int = 0

    // 由于创建了很多个Fragment，所以这个也不是唯一的
    private val moveRecycleState by viewModels<MoveRecycleState> {
        MoveRecycleViewModelFactory((mActivity?.application as App).moveRepository)
    }

    private val _filter :FilterOption by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        FilterOption(attackToward = AttackTowardOption.all(), attackAltitude = AttackAltitudeOption.all(), attackDirection = AttackDirectionOption.all())
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
                        editState.selectMove(this)
                    }
                }
            })
        }
    }

    private val gridLayoutManager :GridLayoutManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        GridLayoutManager(requireContext(),4)
    }

    private val _sideList = mutableListOf<MoveForSelect>()

    private lateinit var recycle :RecyclerView

    private var isFirstEnter = true

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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            _sideList.clear()
            val tempCanHand = when(editState.getDeckInSaved()?.deckType){
                DeckType.HAND -> true
                DeckType.GLOVE -> true
                DeckType.SWORD -> false
                null -> true
            }
            // TODO: 准备改成结束在，别人依靠的是在editFragment或者其他数据拿到起始占位，然后这里找的是结束站位的招式
            // TODO: 现在的问题是我也需要按起始站架来筛选招式，tab只是限制了结束站架，然而选择的框是限制起始站架
            // TODO: 招式是可以镜像的（只适用于徒手卡组，剑卡除外），以直拳为例，可以右上->左上,也可以左上->右上，然后左右也是有镜像的，同样是根据是否启用镜像来确认是否反转显示的
            // 又因为我要根据每个序列的招式进行筛选，起始站架和结束站架都是不一样的，所以我需要在这里持有徒手或剑卡的所有数据，然后在这里进行筛选
            // whatSide 还是当成结束站架
            // 外面会传入一个flow，是起始站架，然后根据这个进行筛选招式
            // TODO: 首先判断是不是找剑卡，假如不是，那就是找徒手卡组
            // TODO: 如果是徒手卡组，则判断起始站架，寻找所有和原本站架和镜像站架相关的招式，找到之后将不和原本站架相同的招式的结束站架和左右全部转成镜像的数据
            // TODO: 最后再根据结束站架分发list
            _sideList.addAll(moveRecycleState.originListByEndSideAndType(whatSide, canHand = tempCanHand, canSword = !tempCanHand).map { MoveForSelect(it) })// 先获取招式列表，再进行监听
            editState.filterOptionFlow.collectLatest {
                Log.i(TAG, "side->${SideUtil.getSideByInt(whatSide)} 接受到筛选数据: Toward->${it.attackToward.name}" +
                        " Altitude->${it.attackAltitude.name} Direction->${it.attackDirection.name}")
                if (_filter.isFilterSame(it)){
                    // 一样的数据，不用变动
                    Log.i(TAG, "side->${SideUtil.getSideByInt(whatSide)} editState.filterOptionFlow: 数据和内部的一样，不需要动_filter")
                    if (isFirstEnter){
                        Log.i(TAG, "filterOptionFlow: isFirst->$isFirstEnter 第一次进来，还是要获取数据")
                        isFirstEnter = false
                        filterList(_sideList,_filter)
                    }
                } else {
                    // 不一样，要重新筛选
                    Log.i(TAG, "side->${SideUtil.getSideByInt(whatSide)} editState.filterOptionFlow: 不一样，_filter要重新设置")
                    _filter.changeAll(it)
                    // 根据sideList来进行筛选
                    filterList(_sideList,_filter)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView:  side ${SideUtil.getSideByInt(whatSide)}")
    }

    /**根据filterOption来筛选list*/
    private fun filterList(sideList: MutableList<MoveForSelect>, option: FilterOption) {
        sideList.filter {
            when(option.attackToward){
                is AttackTowardOption.left -> { it.moveOrigin.attackToward == AttackToward.LEFT }
                is AttackTowardOption.right -> { it.moveOrigin.attackToward == AttackToward.RIGHT }
                is AttackTowardOption.all -> true
            }
        }.filter {
            when(option.attackAltitude){
                is AttackAltitudeOption.height -> { it.moveOrigin.attackAltitude == AttackAltitude.HEIGHT }
                is AttackAltitudeOption.middle -> { it.moveOrigin.attackAltitude == AttackAltitude.MIDDLE }
                is AttackAltitudeOption.low -> { it.moveOrigin.attackAltitude == AttackAltitude.LOW }
                is AttackAltitudeOption.all -> true
            }
        }.filter {
            when(option.attackDirection){
                is AttackDirectionOption.horizontal -> { it.moveOrigin.attackDirection == AttackDirection.HORIZONTAL }
                is AttackDirectionOption.vertical -> { it.moveOrigin.attackDirection == AttackDirection.VERTICAL }
                is AttackDirectionOption.poke -> { it.moveOrigin.attackDirection == AttackDirection.POKE }
                is AttackDirectionOption.all -> true
            }
        }.apply {
            moveAdapter.submitList(this)
        }

    }
}