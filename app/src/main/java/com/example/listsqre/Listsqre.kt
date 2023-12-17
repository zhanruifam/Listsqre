package com.example.listsqre

class Listsqre {
    class Node(private var id: Int, private var listname: String) {
        private lateinit var displayname: String
        fun getId(): Int {
            return id
        }
        fun getListname(): String {
            return listname
        }
        fun setId(newid: Int) {
            id = newid
        }
        fun getDisplayname(): String {
            return displayname
        }
        fun setDisplayname(newlistname: String) {
            displayname = newlistname
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
        fun addNode(listname: String, displayname: String) {
            mutableList.add(Node(idGen++, listname))
            getRecent().setDisplayname(displayname)
        }
        fun pushToSelList(id: Int) {
            selectedList.add(mutableList[id])
        }
        fun removeFromSelList(nodeToRemove: Node) {
            if(selectedList.contains(nodeToRemove)) {
                selectedList.remove(nodeToRemove)
            } else { /* do nothing */ }
        }
        fun getRecent(): Node {
            return mutableList.last()
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
            return "Reminder for the following list(s):"
        }
        fun createNotiDescr(): String {
            var descr = ""
            if(selectedList.isEmpty()) {
                return descr
            } else {
                for(obj in selectedList) {
                    descr += obj.getDisplayname()
                    if(obj != selectedList.last()) {
                        descr += ", "
                    } else { /* do nothing */ }
                }
            }
            return descr
        }
        fun hintStr(): String {
            var hintStr = if(mutableList.isNotEmpty()) {
                "Please avoid: \n"
            } else {
                R.string.description.toString()
            }
            for(obj in mutableList) {
                hintStr += "\"" + obj.getListname() + "\""
                if(obj != mutableList.last()) {
                    hintStr += "\n"
                } else { /* do nothing */ }
            }
            return hintStr
        }
        private fun reassignTaskID() {
            for((iterator, obj) in getEntireList().withIndex()) {
                obj.setId(iterator)
            }
        }
    }
}