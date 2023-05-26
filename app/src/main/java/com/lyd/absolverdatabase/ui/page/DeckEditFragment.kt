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
import com.lyd.absolverdatabase.bridge.data.bean.EditToSelectMsg
import com.lyd.absolverdatabase.bridge.state.DeckEditState
import com.lyd.absolverdatabase.bridge.state.DeckEditViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentDeckEditBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.ui.widgets.DeckDetailDialog
import com.lyd.absolverdatabase.utils.DeckGenerate
import com.lyd.absolverdatabase.utils.TimeUtils
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

        dataBinding?.apply {
            deckEditBarUpperRight.initClick(clickProxy = { view: View ->
                beforeMoveToSelect(EditToSelectMsg.SEQ_UPPER_RIGHT)
            })
            deckEditBarUpperLeft.initClick(clickProxy = {
                beforeMoveToSelect(EditToSelectMsg.SEQ_UPPER_LEFT)
            })
            deckEditBarLowerLeft.initClick(clickProxy = {
                beforeMoveToSelect(EditToSelectMsg.SEQ_LOWER_LEFT)
            })
            deckEditBarLowerRight.initClick(clickProxy = {
                beforeMoveToSelect(EditToSelectMsg.SEQ_LOWER_RIGHT)
            })


            deckEditOptionalUpperRight.initClick(clickProxy = {view: View ->
                beforeMoveToSelect(EditToSelectMsg.OPT_UPPER_RIGHT)
            })
            deckEditOptionalUpperLeft.initClick(clickProxy = {
                beforeMoveToSelect(EditToSelectMsg.OPT_UPPER_LEFT)
            })
            deckEditOptionalLowerLeft.initClick(clickProxy = {
                beforeMoveToSelect(EditToSelectMsg.OPT_LOWER_LEFT)
            })
            deckEditOptionalLowerRight.initClick(clickProxy = {
                beforeMoveToSelect(EditToSelectMsg.OPT_LOWER_RIGHT)
            })

            deckEditeFabSave.setOnClickListener { view ->
                showShortToast("保存卡组")
                editState.saveDeckInSaved(_deckForEdit.copy(updateTime = TimeUtils.curTime))
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
            editState.sequenceUpperRight.collectLatest {
                Log.i(TAG, "接收sequenceUpperRight: $it")
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            editState.sequenceUpperLeft.collectLatest {
                Log.i(TAG, "sequenceUpperLeft: $it")
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            editState.sequenceLowerLeft.collectLatest {
                Log.i(TAG, "sequenceLowerLeft: $it")
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            editState.sequenceLowerRight.collectLatest {
                Log.i(TAG, "sequenceLowerRight: $it")
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            launch {
                editState.optUpperRight.collectLatest {
                    Log.i(TAG, "optUpperRight-> $it")
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            launch {
                editState.optUpperLeft.collectLatest {
                    Log.i(TAG, "optUpperLeft-> $it")
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            launch {
                editState.optLowerLift.collectLatest {
                    Log.i(TAG, "optLowerLift-> $it")
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            launch {
                editState.optLowerRight.collectLatest {
                    Log.i(TAG, "optLowerRight-> $it")
                }
            }
        }

        // 注意，这个必须放在最后，免得发射的时间比建立监听的时间还快
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            editState.deckInSaved.collectLatest { deckInSave ->
                // 这里能够获取到最新的编辑卡组的数据
                // 不论怎么样，都是需要变更布局的
                if (deckInSave == DeckGenerate.generateEmptyDeck(isFromDeckToEdit = true)){
                    // 可以判断为是空卡组，是从deckFragment跳转进来的
                    _deckForEdit = args.deckForEdit
                    Log.i(TAG, "deckInSaved: 从deckFragment跳转进来的，进行数据存储，然后返回")
                    editState.saveDeckInSaved(_deckForEdit.also { if (it.updateTime == 1L) it.updateTime = 0L})
                    return@collectLatest
                } else {
                    // 不相等，是从其他界面切回来的，因为假如是从deckFragment跳转，则会将其设置为空卡组且isFromDeckToEdit = true
                    _deckForEdit = deckInSave
                    Log.i(TAG, "deckInSaved: 不相等，是从其他界面切回来的")
                }
                // 这里应该触发招式序列list的变化，进行初始化，让bar自己判断需不需要变更bg
                editState.updateAllSequence(_deckForEdit)
                editState.updateAllOption(_deckForEdit)
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        dataBinding = null
    }

    private fun beforeMoveToSelect(@androidx.annotation.IntRange(0,7) whatForEdit: Int){
        editState.initFilterOption()
        editState.initSelectMove()
        nav().navigate(DeckEditFragmentDirections.actionDeckEditFragmentToMoveSelectFragment(
            EditToSelectMsg(whatForEdit/*,_deckForEdit.deckType*/)
        ))
    }
}