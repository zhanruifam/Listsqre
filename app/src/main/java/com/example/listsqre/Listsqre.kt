package com.example.listsqre

class Listsqre {
    class Node(private var id: Int, private var listname: String) {
        fun getId(): Int {
            return id
        }

        fun getListname(): String {
            return listname
        }

        fun setId(newid: Int) {
            id = newid
        }

        fun setListname(newlistname: String) {
            listname = newlistname
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