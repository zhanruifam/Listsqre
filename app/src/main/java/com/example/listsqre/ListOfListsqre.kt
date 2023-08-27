package com.example.listsqre

class ListOfListsqre {
    class Node(private var id: Int, private var elemname: String) {
        fun getId(): Int {
            return id
        }

        fun getElemname(): String {
            return elemname
        }

        fun setId(newid: Int) {
            id = newid
        }

        fun setElemname(newelemname: String) {
            elemname = newelemname
        }

        fun fileFormatted(): String {
            return elemname + GlobalVar.DELIMITER
        }
    }

    // CRUD operations and other backend processes
    companion object {
        private var idGen: Int = 0
        private var mutableList = mutableListOf<Node>()

        fun addNode(listname: String) {
            mutableList.add(Node(idGen++, listname))
        }

        fun deleteNode(id: Int) {
            idGen--
            mutableList.removeAt(id)
            reassignTaskID()
        }

        fun getRecent(): Node {
            return mutableList.last()
        }

        fun getEntireList(): List<Node> {
            return mutableList.toList()
        }

        fun deleteAllNodes() {
            idGen = 0
            while(getEntireList().isNotEmpty()) {
                mutableList.removeAt(0)
            }
        }

        private fun reassignTaskID() {
            for((iterator, obj) in getEntireList().withIndex()) {
                obj.setId(iterator)
            }
        }
    }
}