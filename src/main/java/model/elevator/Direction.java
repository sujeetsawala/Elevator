package model.elevator;

public enum Direction {
    UP {
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitUp();
        }
    } ,
    DOWN {
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitDown();
        }
    },
    IDLE {
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitIdle();
        }
    };

    public abstract <T> T accept(Visitor<T> visitor);

    public interface Visitor<T> {
        T visitUp();

        T visitDown();

        T visitIdle();
    }
};
