package com.digiwh.tprod.utils

import android.content.Context
import com.digiwh.tprod.room.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

interface DataExporter{
    suspend fun exportFromDb(data: List<Item>) : XSSFWorkbook
}

class DataExporterImpl(val context : Context):  DataExporter{

    override suspend fun exportFromDb(data: List<Item>): XSSFWorkbook {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Data")

        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Bag No")
        headerRow.createCell(1).setCellValue("Trough No")
        headerRow.createCell(2).setCellValue("KGs")
        headerRow.createCell(3).setCellValue("Date")

        for (i in data.indices) {
            val rowData = sheet.createRow(i + 1)
            rowData.createCell(0).setCellValue(data[i].bagNo.toString())
            rowData.createCell(1).setCellValue(data[i].troughNo.toString())
            rowData.createCell(2).setCellValue(data[i].kgs.toString())
            rowData.createCell(3).setCellValue(data[i].date.toString())
        }
        return workbook
    }

}