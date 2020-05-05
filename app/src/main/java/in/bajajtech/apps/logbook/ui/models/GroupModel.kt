package `in`.bajajtech.apps.logbook.ui.models

class GroupModel {
    private var groupId: Int = 0
    private var groupName: String = ""
    var parties = mutableListOf<PartyModel>()

    fun setGroupId(mId: Int) {
        groupId = mId
    }

    fun getGroupId(): Int = groupId

    fun setGroupName(mGroupName: String) {
        groupName = mGroupName
    }

    fun getGroupName(): String = groupName

}