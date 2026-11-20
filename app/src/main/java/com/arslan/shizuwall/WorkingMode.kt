package com.arslan.shizuwall

enum class WorkingMode {
    SHIZUKU,
    LADB,
    ROOT,
    VPN;

    companion object {
        fun fromName(name: String?): WorkingMode {
            return try {
                if (name == null) SHIZUKU else valueOf(name)
            } catch (e: Exception) {
                SHIZUKU
            }
        }
    }
}
