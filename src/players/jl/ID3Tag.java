package players.jl;

import java.io.File;
import java.io.IOException;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

public class ID3Tag
{
	private MP3File mp3;
	private AbstractID3v2 id3v2;
	
	public ID3Tag(String path)
	{
		File file = new File(path);
		try
		{
			mp3 = new MP3File("C:\\Program Files\\Java\\jid3lib-0.5.4\\music\\Theme - HeMan.mp3");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (TagException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		System.out.println(mp3);
		id3v2 = mp3.getID3v2Tag();
	}
	
	public String getArtist()
	{
		return id3v2.getLeadArtist();
	}
	
	public static void main(String...args)
	{
		ID3Tag tag = new ID3Tag("D:\\Musik\\2Pac feat. Dr Dre");
		System.out.println(tag.getArtist());
	}
}
