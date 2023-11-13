package com.example.listsqre

class ListsqrePlanned {
    class Node(private var id: Int, private var desc: String, private var disp: String) {
        fun getId(): Int {
            return id
        }

        fun getDesc(): String {
            return desc
        }

        fun getDisp(): String {
            return "-> from \"$disp\" list"
        }
    }

    companion object {
        private var idGen: Int = 0
        private var plannedList = mutableListOf<Node>()

        fun getIdGen(): Int {
            return idGen
        }

        fun addNode(desc: String, disp: String) {
            if(!checkDuplicate(desc)) {
                plannedList.add(Node(idGen++, desc, disp))
            } else {
                // do nothing
            }
        }

        fun getEntireList(): List<Node> {
            return plannedList.toList()
        }

        fun clearPlannedList() {
            plannedList.clear()
            idGen = 0
        }

        private fun checkDuplicate(checkDesc: String): Boolean {
            // this function only checks for duplicate desc, needs a better method
            for(obj in plannedList) {
                if(checkDesc == obj.getDesc()) {
                    return true
                }
            }
            return false
        }
    }
}