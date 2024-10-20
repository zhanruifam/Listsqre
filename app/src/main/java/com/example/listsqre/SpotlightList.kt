package com.example.listsqre

class SpotlightList {
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
    }

    // CRUD operations and other backend processes
    companion object { // note: companion object contents are static
        private var idGen: Int = 0
        private var mutableList = mutableListOf<Node>()
        private var selectedList = mutableListOf<Node>()
        var empty: Boolean = true
        private fun deleteNode(id: Int) {
            idGen--
            mutableList.removeAt(id)
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
        fun addNode(listname: String) {
            mutableList.add(Node(idGen++, listname))
            empty = false
        }
        fun pushToSelList(id: Int) {
            selectedList.add(mutableList[id])
        }
        fun removeFromSelList(nodeToRemove: Node) {
            if(selectedList.contains(nodeToRemove)) {
                selectedList.remove(nodeToRemove)
            } else { /* do nothing */ }
        }
        fun getEntireList(): List<Node> {
            return mutableList.toList()
        }
        fun getEntireSelList(): List<Node> {
            return selectedList.toList()
        }
        fun deleteSelNodes() {
            for(node in selectedList) {
                deleteNode(node.getId())
            }
            selectedList.clear()
        }
        fun clrSelList() {
            selectedList.clear()
        }
        private fun reassignTaskID() {
            for((iterator, obj) in getEntireList().withIndex()) {
                obj.setId(iterator)
            }
        }
    }
}