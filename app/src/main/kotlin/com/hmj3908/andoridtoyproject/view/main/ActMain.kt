package com.hmj3908.andoridtoyproject.view.main

import com.hmj3908.andoridtoyproject.base.BaseContractActivity
import com.hmj3908.andoridtoyproject.databinding.ActMainBinding

/**
 * Main Activity
 */
class ActMain : BaseContractActivity<ActMainBinding>() {

    override fun getViewBinding() = ActMainBinding.inflate(layoutInflater)

    override fun initData() {

        fullScreen = false // 풀 스크린
    }

    override fun initView() {

        initUI()
    }

    /**
     * Initialize UI
     */
    private fun initUI() {

        with(mBinding) {

        }
    }
}