package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.Deck
import com.lyd.absolverdatabase.bridge.state.DeckEditState
import com.lyd.absolverdatabase.bridge.state.DeckEditViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentDeckEditBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.ui.widgets.DeckDetailDialog
import com.lyd.absolverdatabase.utils.DeckGenerate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeckEditFragment :BaseFragment() {

    private val editState : DeckEditState by navGraphViewModels(navGraphId = R.id.nav_deck, factoryProducer = {
        DeckEditViewModelFactory((mActivity?.application as App).deckEditRepository)
    })

    private val args :DeckEditFragmentArgs by navArgs()

    private val deckDetailDialog :DeckDetailDialog by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        DeckDetailDialog(requireActivity())
    }

    private var dataBinding :FragmentDeckEditBinding ?= null

    private lateinit var _deckForEdit :Deck /*= DeckGenerate.generateEmptyDeck()*/


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_deck_edit,container,false)

        dataBinding = FragmentDeckEditBinding.bind(view)
        dataBinding?.lifecycleOwner = viewLifecycleOwner

        Log.i(TAG, "onCreateView: ${args.deckForEdit}")
        _deckForEdit= args.deckForEdit


        if (_deckForEdit.createTime == 0L){// 这个deck是个空壳
            // 是创建流程

//            viewLifecycleOwner.lifecycleScope.launch {
//                editState.getOriginListByIdsTest(listOf(0,1,2)).apply {
//                    dataBinding?.deckEditBarUpperRight?.initOriginMoves(this)
//                }
//            }
        } else {
            // 编辑卡组
        }

        dataBinding?.apply {
            deckEditBarUpperRight.setOnClickListener {
                nav().navigate(DeckEditFragmentDirections.actionDeckEditFragmentToMoveSelectFragment())
            }
        }

        dataBinding?.apply {
            deckEditConstrainBg?.setOnLongClickListener {view ->
                deckDetailDialog.apply { mDeck = args.deckForEdit }.show()
                return@setOnLongClickListener true
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            editState.editEventState.collectLatest { event->
                when(event){
                    1 ->{
                        Log.i(TAG, "editEventState: 从deckFragment点进来的")
                    }
                    2 ->{
                        Log.i(TAG, "editEventState: 从moveSelect那回退来的")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            editState.sequenceUpperRight.collectLatest {

            }
        }



    }


    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        dataBinding = null
    }
}