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
            return "from \"$disp\" list"
        }
    }

    companion object {
        private var idGen: Int = 0
        private var plannedList = mutableListOf<Node>()

        fun getIdGen(): Int {
            return idGen
        }

        fun addNode(desc: String, disp: String) {
            plannedList.add(Node(idGen++, desc, disp))
        }

        fun getEntireList(): List<Node> {
            return plannedList.toList()
        }

        fun clearPlannedList() {
            plannedList.clear()
            idGen = 0
        }
    }
}