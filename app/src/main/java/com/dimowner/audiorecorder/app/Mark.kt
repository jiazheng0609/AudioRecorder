package com.dimowner.audiorecorder.app

import android.content.Context
import android.util.Log
import com.dimowner.audiorecorder.util.FileUtil
import com.dimowner.audiorecorder.util.TimeUtils
import java.io.File

const val FPS = 30

class Mark {

    private lateinit var fileSrt: File
    private lateinit var fileEdl: File
    private var contentOut: String = ""
    private var _name: String = ""
    private var counter: Int = 0

    private var timecodes: MutableList<Long> = mutableListOf()

    fun getInstance(context: Context) {

    }

    fun startMark(context: Context, name: String) {
        _name = name
        var filenameExt = name + ".srt"
        var path = FileUtil.getPrivateRecordsDir(context).absolutePath +  filenameExt
        fileSrt = FileUtil.createFile(FileUtil.getPrivateRecordsDir(context), filenameExt)
        Log.d("jiazheng.mark", "mark start called " + path)

        filenameExt = name + ".edl"
        path = FileUtil.getPrivateRecordsDir(context).absolutePath +  filenameExt
        fileEdl = FileUtil.createFile(FileUtil.getPrivateRecordsDir(context), filenameExt)
        Log.d("jiazheng.mark", "mark start called " + path)


    }
    fun addMarkPoint(timeMills: Long, msg: String) {
        counter = counter + 1
        timecodes.add(timeMills)
    }

    fun finishEdlMark(name: String) {
        contentOut = "TITLE: " + name + '\n' + "FCM: NON-DROP FRAME\n\n"
        counter = 1
        for (timeMills in timecodes) {
            var timeStart = TimeUtils.formatSMPTETime(timeMills, FPS)
            var timeEnd = TimeUtils.formatSMPTETime(timeMills+1/FPS*1000, FPS)
            contentOut = contentOut + counter.toString() + " 001 V C " + timeStart + " " + timeEnd + " "+ timeStart + " " + timeEnd + '\n' + "mark1" + " |C:ResolveColorBlue |M: |D:" + FPS.toString() + "\n\n"
            counter = counter + 1
        }
    }

    fun finishSrtMark(name: String) {
        counter = 1
        for (timeMills in timecodes) {
            var timeStart = TimeUtils.formatSrtTime(timeMills)
            var timeEnd = TimeUtils.formatSrtTime(timeMills+1)
            contentOut = contentOut + counter.toString() + '\n' + timeStart + " --> " + timeEnd  + '\n' + "mark1" + "\n\n"
            counter = counter + 1
        }

    }

    fun finishMark(name: String) {
        finishSrtMark(name)
        fileSrt.writeText(contentOut)
        contentOut = ""

        finishEdlMark(name)
        fileEdl.writeText(contentOut)
        contentOut = ""

        if (name != _name)
            renameMark(name)

        _name = ""
    }

    fun renameMark(newName: String) {
        if (FileUtil.renameFile(fileSrt, newName, "srt")) {
            Log.d("jiazheng.mark", "rename " + fileSrt.absolutePath + " to " + newName)
        }
        if (FileUtil.renameFile(fileEdl, newName, "edl")) {
            Log.d("jiazheng.mark", "rename " + fileSrt.absolutePath + " to " + newName)
        }
    }

}