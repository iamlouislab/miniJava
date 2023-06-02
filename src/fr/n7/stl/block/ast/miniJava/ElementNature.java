package fr.n7.stl.block.ast.miniJava;

public enum ElementNature {

    None,
    Static,
    Final,
    StaticFinal,
    Abstract;

    public String toString() {
        switch (this) {
            case None:
                return "";
            case Static:
                return "static ";
            case Final:
                return "final ";
            case StaticFinal:
                return "static final";
            case Abstract:
                return "abstract";
            default:
                throw new IllegalArgumentException("The default case should never be triggered.");
        }
    }

}
