package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.state.DeckEditState
import com.lyd.absolverdatabase.bridge.state.DeckEditViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentMoveSelectBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest

class MoveSelectFragment :BaseFragment(){

    private val editState :DeckEditState by navGraphViewModels(navGraphId = R.id.nav_deck, factoryProducer = {
        DeckEditViewModelFactory((mActivity?.application as App).deckEditRepository)
    })

    private var dataBinding : FragmentMoveSelectBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view :View = inflater.inflate(R.layout.fragment_move_select,container,false)

        dataBinding = FragmentMoveSelectBinding.bind(view)
        dataBinding?.lifecycleOwner = viewLifecycleOwner
        dataBinding?.vm = editState

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            editState.editEventState.emit(2)// 发射数据，告诉EditFragment等会是从这里回退
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataBinding = null
    }
}