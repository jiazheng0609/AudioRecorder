package com.dimowner.audiorecorder.app

import android.content.Context
import android.util.Log
import com.dimowner.audiorecorder.util.FileUtil
import com.dimowner.audiorecorder.util.TimeUtils
import java.io.File

class Mark {

    private lateinit var file: File
    private var contentOut: String = ""
    private var counter: Int = 0
    private var path: String = ""

    fun getInstance(context: Context) {

    }

    fun startMark(context: Context, filename: String) {
        var filenameExt = filename + ".srt"
        contentOut = ""
        path = FileUtil.getPrivateRecordsDir(context).absolutePath +  filenameExt
        file = FileUtil.createFile(FileUtil.getPrivateRecordsDir(context), filenameExt)
        Log.d("jiazheng.mark", "mark start called "+path)


    }
    fun addMarkPoint(timeMills: Long, msg: String) {
        counter = counter + 1
        var timeStart = TimeUtils.formatSrtTime(timeMills)
        var timeEnd = TimeUtils.formatSrtTime(timeMills+1000)
        contentOut = contentOut + counter.toString() + '\n' + timeStart + " --> " + timeEnd  + '\n' + msg + "\n\n"
    }

    fun finishMark() {
        file.writeText(contentOut)
    }

    fun renameMark(newName: String) {
        FileUtil.renameFile(file, newName, "srt")
        Log.d("jiazheng.mark", "rename " + path + " to " + newName)
    }

}