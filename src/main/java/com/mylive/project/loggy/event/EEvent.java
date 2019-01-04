package com.mylive.project.loggy.event;

public enum EEvent {
    ERROR {
        @Override
        public String getColor() {
            return "red";
        }
    },
    INFO {
        @Override
        public String getColor() {
            return "grey";
        }
    },
    DEBUG {
        @Override
        public String getColor() {
            return "blue";
        }
    },
    WARN {
        @Override
        public String getColor() {
            return "orange";
        }
    },
    FATAL {
        @Override
        public String getColor() {
            return "red";
        }
    };

    public abstract String getColor();

    }
