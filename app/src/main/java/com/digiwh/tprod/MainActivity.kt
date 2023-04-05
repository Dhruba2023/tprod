package com.digiwh.tprod

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.digiwh.tprod.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("UNUSED_EXPRESSION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding : ActivityMainBinding

    private val mViewModel by lazy {
        ViewModelProvider(this)[TProdViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initViews()
        initLifeCycle()
    }

    private fun initLifeCycle() {
        lifecycleScope.launchWhenCreated {
            mViewModel.uiState.collectLatest {
                if(it.name != null){
                    startTProdActivity()
                }
            }
        }
        mViewModel.init()
    }

    private fun initViews() {
        mBinding.eTName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mViewModel.saveName(mBinding.eTName.text.toString())
                startTProdActivity()
                true
            }
            false
        }
    }

    private fun startTProdActivity() {
        finish()
        startActivity(Intent(this, TProdActivity::class.java))
    }

}