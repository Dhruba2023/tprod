package com.digiwh.tprod

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digiwh.MainApplication
import com.digiwh.tprod.room.Databases
import com.digiwh.tprod.room.Item
import com.digiwh.tprod.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TProdViewModel @Inject constructor(
    private val application: MainApplication,
    private val dateChecker: DateChecker,
    private val room: Databases,
    private val dataExporter: DataExporter,
) : ViewModel() {

    data class State(
        var name: String? = null,
        var bagNo: Long? = null,
        var date: String? = null,
    )

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State())
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    fun init() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                name = PrefManager.getString(application, PREF_NAME),
                bagNo = PrefManager.getLong(application, PREF_BAG_NO),
                date = DateUtils.getDateString(Date(), DATE_FORMAT)
            )
        }
    }

    fun saveName(name: String) {
        PrefManager.saveString(application, PREF_NAME, name)
    }

    fun isFieldsValidated(date: String, troughNo: String, kgs: String): Boolean =
        date.isNotEmpty() && troughNo.isNotEmpty() && kgs.isNotEmpty()

    fun saveData(date: String, troughNo: String, kgs: String) = viewModelScope.launch {
        CoroutineScope(Dispatchers.IO).launch {
            room.defaultDao.insert(
                Item(
                    bagNo = PrefManager.getLong(application, PREF_BAG_NO),
                    troughNo = troughNo.toLong(),
                    kgs = kgs.toDouble(),
                    date = date
                )
            )

            PrefManager.saveLong(application, PREF_BAG_NO, uiState.value.bagNo?.plus(1) ?: 1)

            _uiState.update {
                it.copy(
                    bagNo = PrefManager.getLong(application, PREF_BAG_NO)
                )
            }
        }
    }

    fun saveDataToExcelFile(uri: Uri) = viewModelScope.launch {
        CoroutineScope(Dispatchers.IO).launch {
            val contentResolver = application.contentResolver
            try {
                val outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    dataExporter.exportFromDb(room.defaultDao.allItems()).write(outputStream)
                    withContext(Dispatchers.IO) {
                        outputStream.close()
                    }
                }
                room.defaultDao.nukeTable()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun validateDate(onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}) =
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                dateChecker.checkSystemDate(
                    onSuccess = {
                        CoroutineScope(Dispatchers.Main).launch {
                            onSuccess.invoke()
                        }
                    }, onFailure = {
                        CoroutineScope(Dispatchers.Main).launch {
                            onFailure.invoke()
                        }
                    }
                )
            }
        }

}