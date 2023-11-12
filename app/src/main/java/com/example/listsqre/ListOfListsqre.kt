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
        private var selectedList = mutableListOf<Node>()

        private fun deleteNode(id: Int) {
            idGen--
            mutableList.removeAt(id)
            reassignTaskID()
        }

        fun addNode(listname: String) {
            mutableList.add(Node(idGen++, listname))
        }

        fun pushToSelList(id: Int) {
            selectedList.add(mutableList[id])
        }

        fun removeFromSelList(nodeToRemove: Node) {
            if(selectedList.contains(nodeToRemove)) {
                selectedList.remove(nodeToRemove)
            } else {
                // do nothing
            }
        }

        fun getEntireList(): List<Node> {
            return mutableList.toList()
        }

        fun deleteAllNodes() { // obsolete
            idGen = 0
            while(getEntireList().isNotEmpty()) {
                mutableList.removeAt(0)
            }
        }

        fun deleteSelNodes() {
            for(node in selectedList) {
                deleteNode(node.getId())
            }
            selectedList.clear()
        }

        fun clearSelList() {
            selectedList.clear()
        }

        private fun reassignTaskID() {
            for((iterator, obj) in getEntireList().withIndex()) {
                obj.setId(iterator)
            }
        }
    }
}