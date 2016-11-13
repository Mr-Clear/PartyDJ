package de.klierlinge.partydj.pjr.beans;

public class Setting extends Message
{
    public final String name;
    public final String value;

    public Setting()
    {
        name = null;
        value = null;
    }
    
    public Setting(final String name, final String value)
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
