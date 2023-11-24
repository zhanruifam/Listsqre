package com.example.listsqre

class ListsqrePlanned {
    class Node(private var id: Int, private var desc: String, private var disp: String) {
        fun setId(newid: Int) {
            id = newid
        }

        fun getId(): Int {
            return id
        }

        fun setDesc(newDesc: String) {
            desc = newDesc
        }

        fun getDesc(): String {
            return desc
        }

        fun setDisp(newDisp: String) {
            disp = newDisp
        }

        fun getDisp(): String {
            return disp
        }
    }

    companion object {
        private var idGen: Int = 0
        private var plannedList = mutableListOf<Node>()
        private var selectedList = mutableListOf<Node>()

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

        fun getEntireList(): List<Node> {
            return plannedList.toList()
        }

        fun getEntireSelList(): List<Node> {
            return selectedList.toList()
        }

        fun clrPlannedList() {
            plannedList.clear()
        }

        fun clrSelList() {
            selectedList.clear()
        }

        fun planLTextCaption(): String {
            return plannedList.size.toString() + " Planned Item(s)"
        }

        fun setChangedDisp(strToChange: String, newDisp: String) { // O(n) to be optimized
            for(obj in plannedList) {
                if(obj.getDisp() == strToChange) {
                    obj.setDisp(newDisp)
                } else {
                    // do nothing
                }
            }
        }

        private fun reassignTaskID() {
            for((iterator, obj) in getEntireList().withIndex()) {
                obj.setId(iterator)
            }
        }
    }
}