package org.example.meta;

import lombok.Getter;

@Getter
public enum MetaData {
    CTRLID("//"),
    COMMENTID("//*"),
    NULLID("/*");

    private final String id;

    MetaData(String id) {
        this.id = id;
    }
}