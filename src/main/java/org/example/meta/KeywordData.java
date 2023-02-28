package org.example.meta;

import lombok.Getter;

@Getter
public enum KeywordData {

    JOB("JOB", Type.JOB),
    DD("DD", Type.DD);

    private final String operation;
    private final Type type;

    KeywordData(String operationName, Type operationType) {
        this.operation = operationName;
        this.type = operationType;
    }

    public static boolean checkNextModeIsParm(String operation) {
        return JOB.getOperation().equals(operation) | DD.getOperation().equals(operation);
    }
}
