package cc.mashroom.hedgehog.system;

import  lombok.AllArgsConstructor;
import  lombok.Getter;

@AllArgsConstructor

public  enum   MediaType
{
	IMAGE(1),VIDEO(2);
	@Getter
	private  int  value;

	public  static  MediaType  valueOf( int  value )
	{
		for( MediaType  packetType : MediaType.values() )
		{
			if( value == packetType.getValue() )
			{
				return  packetType;
			}
		}
		
		throw  new  IllegalArgumentException( String.format("MASHROOM-PARENT:  ** MEDIA  TYPE **  no  media  type  defined  for  %d",value) );
	}
}
