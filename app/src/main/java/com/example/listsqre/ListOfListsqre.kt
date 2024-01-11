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
        fun createNotiTitle(): String {
            var title = ""
            title += if(selectedList.isEmpty()) {
                "Check Listsqre"
            } else {
                "Check item(s):"
            }
            return title
        }
        fun createNotiDescr(): String {
            var descr = ""
            if(selectedList.isEmpty()) {
                return descr
            } else {
                for(obj in selectedList) {
                    descr += obj.getElemname()
                    if(obj != selectedList.last()) {
                        descr += ", "
                    } else { /* do nothing */ }
                }
            }
            return descr
        }
        private fun reassignTaskID() {
            for((iterator, obj) in getEntireList().withIndex()) {
                obj.setId(iterator)
            }
        }
    }
}