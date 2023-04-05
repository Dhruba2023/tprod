package com.digiwh.tprod

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.digiwh.tprod.databinding.ActivityTprodBinding
import com.digiwh.tprod.utils.DATE_FORMAT
import com.digiwh.tprod.utils.DATE_TIME_FORMAT
import com.digiwh.tprod.utils.DateUtils
import com.digiwh.tprod.utils.NetworkUtils
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class TProdActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityTprodBinding

    private val mViewModel by lazy {
        ViewModelProvider(this)[TProdViewModel::class.java]
    }

    private val progressDialog by lazy {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setView(R.layout.progress_dialog_layout)
    }

    private val createFileActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                mViewModel.saveDataToExcelFile(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tprod)

        initViews()
        initLifecycle()
    }

    private fun initLifecycle() {
        lifecycleScope.launchWhenCreated {
            mViewModel.uiState.collectLatest {
                mBinding.title.text = getString(R.string.welcome_x, "${it.name}")
                mBinding.bagNo.text = getString(R.string.bag_no_x, "${it.bagNo}")
                mBinding.eTDate.setText(it.date)
            }
        }
    }

    override fun onResume() {
        mViewModel.init()
        super.onResume()
    }

    private fun initViews() {
        mBinding.eTDate.inputType = InputType.TYPE_NULL
        mBinding.eTDate.keyListener = null

        mBinding.eTDate.setOnClickListener {

            val constraintsBuilder = CalendarConstraints.Builder()
            constraintsBuilder.setStart(MaterialDatePicker.todayInUtcMilliseconds())
            constraintsBuilder.setValidator(DateValidatorPointForward.now())

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(Calendar.getInstance().timeInMillis)
                .build()

            datePicker.addOnPositiveButtonClickListener {
                mBinding.eTDate.setText(DateUtils.getDateString(Date(it), DATE_FORMAT))
            }

            datePicker.show(supportFragmentManager, getString(R.string.enter_date))
        }

        mBinding.clearBtn.setOnClickListener {
            mBinding.eTTroughNo.text?.clear()
            mBinding.eTTKgs.text?.clear()
        }

        mBinding.saveBtn.setOnClickListener {
            if (mViewModel.isFieldsValidated(
                    mBinding.eTDate.text.toString(),
                    mBinding.eTTroughNo.text.toString(),
                    mBinding.eTTKgs.text.toString(),
                )
            ) {

                if (NetworkUtils.isInternetConnected(this)) {
                    val dialog = progressDialog.show()

                    mViewModel.validateDate(
                        onSuccess = {
                            dialog.dismiss()

                            mViewModel.saveData(
                                mBinding.eTDate.text.toString(),
                                mBinding.eTTroughNo.text.toString(),
                                mBinding.eTTKgs.text.toString(),
                            )

                            mBinding.eTTroughNo.text?.clear()
                            mBinding.eTTKgs.text?.clear()

                            showDialog(getString(R.string.success), getString(R.string.data_saved))
                        },
                        onFailure = {
                            dialog.dismiss()
                            showDialog(getString(R.string.oops),
                                getString(R.string.please_correct_date_settings)) {
                                startActivity(Intent(Settings.ACTION_DATE_SETTINGS))
                            }
                        }
                    )
                } else {
                    showDialog(getString(R.string.oops),
                        getString(R.string.please_connect_to_internet)) {
                        openInternetSettings(this)
                    }
                }
            } else {
                showDialog(getString(R.string.invalid), getString(R.string.please_enter_all_fields))
            }
        }

        mBinding.downloadBtn.setOnClickListener {
            showDialog(getString(R.string.are_you_sure), getString(R.string.you_want_to_download)) {
                createExcelFile()
            }
        }
    }

    private fun createExcelFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/vnd.ms-excel"
        intent.putExtra(Intent.EXTRA_TITLE,
            "TProd/${
                DateUtils.getDateString(Date(),
                    DATE_TIME_FORMAT)
            }")
        createFileActivityResult.launch(intent)
    }


    private fun showDialog(title: String, message: String, onClick: () -> Unit = {}) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.okay)
            ) { _, _ ->
                onClick.invoke()
            }
            .show()
    }

    private fun openInternetSettings(context: Context) {
        val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

}