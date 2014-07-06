package network.remoteV2.beans;

public class Setting extends Message
{
    public final String name;
    public final String value;

    public Setting()
    {
        name = null;
        value = null;
    }
    
    public Setting(String name, String value)
    {
        super();
        this.name = name;
        this.value = value;
    }

    @Override
    public Child getType()
    {
        return Child.Setting;
    }
}
