package com.example.listsqre

import java.util.Calendar

class NotiOfListsqre {
    class Node(private var id: Int,
               private var title: String,
               private var description: String,
               private var hour: Int,
               private var minute: Int) {
        fun getId(): Int {
            return id
        }
        fun setId(newid: Int) {
            id = newid
        }
        fun getT(): String {
            return title
        }
        fun getD(): String {
            return description
        }
        fun getH(): Int {
            return hour
        }
        fun getM(): Int {
            return minute
        }
        fun getFormattedString(): String {
            val time: String = String.format("~ %02d:%02d", hour, minute)
            return time + "\n" + title + "\n" + description
        }
    }

    // CRUD operations and other backend processes
    companion object { // note: companion object contents are static
        private var idGen: Int = 0
        private var mutableList = mutableListOf<Node>()
        var empty: Boolean = true
        fun deleteNode(id: Int) {
            idGen--
            mutableList.removeAt(id)
            sortWithDay(mutableList)
            reassignTaskID()
            if(mutableList.isEmpty()) {
                idGen = 0
                empty = true
            } else { /* do nothing */ }
        }
        fun deleteAllNodes() { // prevent double creation when start new activity
            idGen = 0
            mutableList.clear()
            empty = true
        }
        fun addNode(t: String, d: String, h: Int, m: Int) {
            if(!duplicateNoti(h, m)) {
                mutableList.add(Node(idGen++, t, d, h, m))
            } else { /* do nothing */ }
            sortWithDay(mutableList)
            reassignTaskID()
            empty = false
        }
        fun getEntireList(): List<Node> {
            return mutableList.toList()
        }
        private fun duplicateNoti(h: Int, m: Int): Boolean {
            for(obj in mutableList) {
                if(h == obj.getH() && m == obj.getM()) {
                    return true
                } else { /* do nothing */ }
            }
            return false
        }
        private fun sortWithDay(list: List<Node>) {
            val currTime = Calendar.getInstance()
            val currHour = currTime.get(Calendar.HOUR_OF_DAY)
            val currMin = currTime.get(Calendar.MINUTE)
            val tmpCurrDay = mutableListOf<Node>()
            val tmpNextDay = mutableListOf<Node>()
            for(obj in list) {
                if(obj.getH() <= currHour && obj.getM() <= currMin) {
                    tmpNextDay.add(obj)
                } else {
                    tmpCurrDay.add(obj)
                }
            }
            tmpCurrDay.sortWith(compareBy({ it.getH() }, { it.getM() }))
            tmpNextDay.sortWith(compareBy({ it.getH() }, { it.getM() }))
            mutableList.clear()
            mutableList.addAll(tmpCurrDay)
            mutableList.addAll(tmpNextDay)
        }
        private fun reassignTaskID() {
            for((iterator, obj) in getEntireList().withIndex()) {
                obj.setId(iterator)
            }
        }
    }
}