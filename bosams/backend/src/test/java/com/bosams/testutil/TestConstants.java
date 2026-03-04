package com.bosams.testutil;

import java.util.UUID;

public final class TestConstants {
    private TestConstants() {}

    public static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID TEACHER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final String JWT_SECRET = "1234567890123456789012345678901234567890123456789012345678901234";
}
