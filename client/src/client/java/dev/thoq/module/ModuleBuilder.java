package dev.thoq.module;

public class ModuleBuilder {
    private final ModuleRepository repository;

    private ModuleBuilder() {
        this.repository = ModuleRepository.getInstance();
    }

    public static ModuleBuilder create() {
        return new ModuleBuilder();
    }

    public void putAll(Module... modules) {
        for(Module module : modules) {
            repository.registerModule(module);
        }
    }
}
