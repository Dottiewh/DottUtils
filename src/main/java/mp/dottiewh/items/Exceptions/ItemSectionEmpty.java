package mp.dottiewh.items.Exceptions;

public class ItemSectionEmpty extends InvalidItemConfigException{
    public ItemSectionEmpty() {
        super("No existe ningun item en Items.yml");
    }
    public ItemSectionEmpty(String note) {
        super("No existe ningun item en Items.yml, Note: "+note);
    }
}
