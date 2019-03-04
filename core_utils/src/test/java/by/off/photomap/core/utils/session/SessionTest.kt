package by.off.photomap.core.utils.session

import by.off.photomap.model.UserInfo
import org.junit.Assert.*
import org.junit.Test

class SessionTest {
    companion object {
        const val USER_ID = "qwerty"
    }

    @Test
    fun testUserValue() {
        Session.user = UserInfo(USER_ID)
        assertNotNull("User must NOT be null", Session.user)
        assertEquals("User ID must be as provided $USER_ID", USER_ID, Session.user.id)
    }
}