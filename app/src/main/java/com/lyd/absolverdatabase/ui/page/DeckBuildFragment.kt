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
import com.lyd.absolverdatabase.bridge.state.DeckState
import com.lyd.absolverdatabase.bridge.state.DeckViewModelFactory
import com.lyd.absolverdatabase.ui.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest

class DeckBuildFragment :BaseFragment() {

    private val viewModel :DeckState by navGraphViewModels(navGraphId = R.id.nav_deck, factoryProducer = {
        DeckViewModelFactory((mActivity?.application as App).deckRepository)
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view :View = inflater.inflate(R.layout.fragment_deck_build,container,false)


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.choiceFlow.collectLatest {
                Log.i(TAG, "viewModel choice -> $it")
            }
        }
    }


}