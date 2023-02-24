package org.example.meta;

import lombok.Getter;

@Getter
public enum KeywordData {

    JOB("JOB", Type.JOB);

    private String operation;
    private Type type;

    KeywordData(String OperationName, Type OperationType) {
        this.operation = OperationName;
        this.type = OperationType;
    }

    public static boolean checkNextModeIsParm(String operation) {
        return JOB.getOperation().equals(operation);
    }
}
