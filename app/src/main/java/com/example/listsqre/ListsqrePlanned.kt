package com.example.listsqre

class ListsqrePlanned {
    class Node(private var id: Int, private var desc: String, private var disp: String) {
        fun setId(newid: Int) {
            id = newid
        }

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
        private var selectedList = mutableListOf<Node>()

        fun getIdGen(): Int {
            return idGen
        }

        fun addNode(desc: String, disp: String) {
            plannedList.add(Node(idGen++, desc, disp))
        }

        private fun deleteNode(id: Int) {
            idGen--
            plannedList.removeAt(id)
            reassignTaskID()
        }

        fun pushToSelList(id: Int) {
            selectedList.add(plannedList[id])
        }

        fun removeFromSelList(nodeToRemove: Node) {
            if(selectedList.contains(nodeToRemove)) {
                selectedList.remove(nodeToRemove)
            } else {
                // do nothing
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

        fun getEntireList(): List<Node> {
            return plannedList.toList()
        }

        private fun reassignTaskID() {
            for((iterator, obj) in getEntireList().withIndex()) {
                obj.setId(iterator)
            }
        }
    }
}