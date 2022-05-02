package com.dimowner.audiorecorder.app

import android.content.Context
import android.util.Log
import com.dimowner.audiorecorder.util.FileUtil
import com.dimowner.audiorecorder.util.TimeUtils
import java.io.File

class Mark {

    private lateinit var file: File
    private var contentOut: String = ""
    private var _name: String = ""
    private var counter: Int = 0
    private var path: String = ""

    fun getInstance(context: Context) {

    }

    fun startMark(context: Context, name: String) {
        _name = name
        var filenameExt = name + ".srt"
        path = FileUtil.getPrivateRecordsDir(context).absolutePath +  filenameExt
        file = FileUtil.createFile(FileUtil.getPrivateRecordsDir(context), filenameExt)
        Log.d("jiazheng.mark", "mark start called " + path)


    }
    fun addMarkPoint(timeMills: Long, msg: String) {
        counter = counter + 1
        var timeStart = TimeUtils.formatSrtTime(timeMills)
        var timeEnd = TimeUtils.formatSrtTime(timeMills+1000)
        contentOut = contentOut + counter.toString() + '\n' + timeStart + " --> " + timeEnd  + '\n' + msg + "\n\n"
    }

    fun finishMark(name: String) {
        file.writeText(contentOut)
        if (name != _name)
            renameMark(name)
        contentOut = ""
        _name = ""
    }

    fun renameMark(newName: String) {
        if (FileUtil.renameFile(file, newName, "srt")) {
            Log.d("jiazheng.mark", "rename " + path + " to " + newName)
        }
    }

}