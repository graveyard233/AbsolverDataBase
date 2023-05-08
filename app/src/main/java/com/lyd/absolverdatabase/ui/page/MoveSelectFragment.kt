package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.state.DeckEditState
import com.lyd.absolverdatabase.bridge.state.DeckEditViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentMoveSelectBinding
import com.lyd.absolverdatabase.ui.adapter.MovePagerAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest

class MoveSelectFragment :BaseFragment(){

    private val editState :DeckEditState by navGraphViewModels(navGraphId = R.id.nav_deck, factoryProducer = {
        DeckEditViewModelFactory((mActivity?.application as App).deckEditRepository)
    })

    private val argMsg :MoveSelectFragmentArgs by navArgs()

    private var dataBinding : FragmentMoveSelectBinding ?= null

    private var barLazy :View ?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view :View = inflater.inflate(R.layout.fragment_move_select,container,false)

        dataBinding = FragmentMoveSelectBinding.bind(view)
        dataBinding?.lifecycleOwner = viewLifecycleOwner
        dataBinding?.vm = editState

        when (argMsg.toSelectMsg.whatBarToEdit){
            in 0..3 ->{// 应该加载带3个按钮的MovesBar
                dataBinding?.moveSelectViewStub?.viewStub?.layoutResource = R.layout.bar_moves
                dataBinding?.guidelineBarBottom?.setGuidelinePercent(0.35F)
            }
            in 4..7 ->{// 应该加载oneMoveBar
                dataBinding?.moveSelectViewStub?.viewStub?.layoutResource = R.layout.bar_one_move
                dataBinding?.guidelineBarBottom?.setGuidelinePercent(0.4F)
            }
        }

        dataBinding?.moveSelectViewStub?.viewStub?.inflate()
        // viewStub加载完成之后，要在onViewCreate那里才能找到加载的view
        dataBinding?.apply {
            val movePagerAdapter = MovePagerAdapter(this@MoveSelectFragment)
            moveSelectPager?.adapter = movePagerAdapter
            val iconList = listOf<Int>(R.drawable.ic_upper_right_bold,R.drawable.ic_upper_left_bold,
            R.drawable.ic_lower_left_bold,R.drawable.ic_lower_right_bold)
            if (moveSelectTab != null) {
                if (moveSelectPager != null) {
                    TabLayoutMediator(moveSelectTab,moveSelectPager){tab, position ->
                        tab.setIcon(iconList[position])
                    }.attach()
                }
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        barLazy = requireView().findViewById(R.id.moveSelect_bar)
        // 在这里可以找到加载布局的控件

//        try {
//            barLazy = requireView().findViewById(R.id.moveSelect_bar)
////            val tempBar :MovesBar = barLazy as MovesBar // 做不到强制转换，因为这不是一个view，而是一个layout，所以要靠
//            val tempImg = barLazy?.findViewById<ImageView>(R.id.bar_move_0)
//            tempImg!!.setImageResource(R.drawable.ic_faejin)
//        } catch (e :Exception){
//            Log.e(TAG, "onCreateView: ", e)
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataBinding = null
    }
}