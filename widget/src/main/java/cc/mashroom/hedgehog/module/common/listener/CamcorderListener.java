package cc.mashroom.hedgehog.module.common.listener;

import  android.content.Intent;
import  android.net.Uri;
import  android.view.MotionEvent;
import  android.view.View;
import  android.widget.Toast;

import  com.facebook.drawee.view.SimpleDraweeView;
import  com.google.common.collect.Lists;
import  com.irozon.sneaker.Sneaker;

import  java.io.File;
import  java.io.Serializable;
import  java.util.concurrent.atomic.AtomicBoolean;

import  cc.mashroom.hedgehog.R;
import  cc.mashroom.hedgehog.device.camera.Camera;
import  cc.mashroom.hedgehog.module.common.activity.CamcorderActivity;
import  cc.mashroom.hedgehog.device.camera.PhotoTakenListener;
import  cc.mashroom.hedgehog.system.Media;
import  cc.mashroom.hedgehog.system.MediaType;
import  cc.mashroom.hedgehog.util.ContextUtils;
import  cc.mashroom.hedgehog.util.ImageUtils;
import  cc.mashroom.hedgehog.widget.ViewSwitcher;
import  cc.mashroom.util.FileUtils;
import  cc.mashroom.util.ObjectUtils;
import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  es.dmoral.toasty.Toasty;
import  lombok.Setter;
import  lombok.experimental.Accessors;
import  me.relex.photodraweeview.PhotoDraweeView;

public  class  CamcorderListener  implements  View.OnTouchListener,View.OnLongClickListener,View.OnClickListener,PhotoTakenListener
{
	public  CamcorderListener(  CamcorderActivity context,/*Lollipop*/Camera  camera,int  mediaType )
	{
		this.setContext(context).setCamera(camera).setMediaType(   mediaType );
	}

	private  Map<Integer,File>  takenMediaFiles = new  HashMap<Integer,File>();

	@Accessors( chain= true )
	@Setter
	private  CamcorderActivity   context;
	@Accessors( chain= true )
	@Setter
	private  Camera   camera;
	@Accessors( chain= true )
	@Setter
	private  int   mediaType;

	private  AtomicBoolean  recordVideo = new  AtomicBoolean();

	public  boolean  onLongClick(  View  recordButton )
	{
		try
		{
			if( mediaType>= 2   && this.recordVideo.compareAndSet(false,true) )
			{
				camera.recordVideo(FileUtils.createFileIfAbsent(new File(context.getCacheDir(),"video.mp4.tmp"),null) );
			}
		}
		catch( Exception  e )
		{
		    context.error(e);

            context.showSneakerWindow( Sneaker.with(context).setOnSneakerDismissListener(() -> context.application().getMainLooperHandler().postDelayed(() -> ContextUtils.finish(context),500)),com.irozon.sneaker.R.drawable.ic_error,R.string.permission_denied,R.color.white,R.color.red );
		}

		return  false;
	}

	public  boolean  onTouch( View  button,MotionEvent  event )
	{
		try
		{
			//  when  touching  down  to  record  video  and  record  audio  permission  not  granted  yet,  grant  permission  dialog  pop  up,  so  {@link  MotionEvent.ACTION_CANCEL}  event  comes  after  granting  permission  on  vivo  (y66).
			if( event.getAction() == MotionEvent.ACTION_CANCEL && this.recordVideo.compareAndSet(          true,false) )
			{
				camera.stopRecordVideo();

				camera.continuePreview();

				return  true;
			}
			else
			if( event.getAction() == MotionEvent.ACTION_UP && recordVideo.compareAndSet(true,false) )
			{
				File  videoFile= this.camera.stopRecordVideo();
				//  touch  event  sometimes  changes  to  {@link  MotionEvent.ACTION_UP}  but  {@link  MotionEvent.ACTION_CANCEL}  after  denial  of  record  audio  permission  on  vivo  (y66).
				if( !videoFile.exists() )
				{
					Toasty.error(this.context,this.context.getString( R.string.permission_denied ),Toast.LENGTH_LONG,false).show();

				camera.continuePreview();

				return  true;
				}

				takenMediaFiles.put( MediaType.VIDEO.getValue(), this.context.application().cache( -1, videoFile, 4 ) );

				ObjectUtils.cast(context.findViewById(R.id.control_switcher),ViewSwitcher.class).setDisplayedChild( 1 );

				return  true;
			}
		}
		catch( Exception  e )
		{
            context.error(e);

            try
            {
                camera.continuePreview();
            }
            catch(     Throwable  error )
            {
                context.error(   error );
            }
            finally
            {
                return  true;
            }
		}

		return  false;
	}

	public  void  onClick( View  button )
	{
		try
		{
			if( button.getId() == R.id.preview_button )
			{
				ObjectUtils.cast(context.findViewById(R.id.preview_switcher),ViewSwitcher.class).setDisplayedChild( 0 );

				this.context.findViewById(R.id.additional_switcher).setVisibility(    View.VISIBLE );

				ObjectUtils.cast(context.findViewById(R.id.control_switcher),ViewSwitcher.class).setDisplayedChild( 0 );

				ObjectUtils.cast(ObjectUtils.cast(context.findViewById(R.id.control_switcher),ViewSwitcher.class).getDisplayedChild(),SimpleDraweeView.class).setClickable( true );

				ObjectUtils.cast(ObjectUtils.cast(context.findViewById(R.id.control_switcher),ViewSwitcher.class).getDisplayedChild(),SimpleDraweeView.class).setImageURI( ImageUtils.toUri(context,      R.drawable.red_placeholder) );

				takenMediaFiles.clear( );

				camera.continuePreview();
			}
			else
			if( button.getId() == R.id.take_picture_or_record_video_button && (this.mediaType == 1 || mediaType == 3 ) )
			{
				/*
				FileUtils.createFileIfAbsent( new File(context.getCacheDir(),"photo.jpg.tmp"),null );
				*/
				camera.takePicture(this);

				ObjectUtils.cast(ObjectUtils.cast(context.findViewById(R.id.control_switcher),ViewSwitcher.class).getDisplayedChild(),SimpleDraweeView.class).setImageURI( ImageUtils.toUri(context,R.drawable.lightgray_placeholder) );
			}
			else
			if( button.getId() == R.id.confirm_button )
			{
				java.util.Map.Entry<Integer,File>  takenMediaFile   = this.takenMediaFiles.entrySet().iterator().next();

				context.putResultDataAndFinish( context,0,new  Intent().putExtra("MEDIAS",ObjectUtils.cast(Lists.newArrayList(new  Media(MediaType.valueOf(takenMediaFile.getKey()),-1,null,null,-1,-1,-1,takenMediaFile.getValue().getPath(),null,-1,-1)),Serializable.class)) );
			}
		}
		catch( Exception  e )
		{
			context.error(e);

            context.showSneakerWindow( Sneaker.with(context).setOnSneakerDismissListener(() -> context.application().getMainLooperHandler().postDelayed(() -> ContextUtils.finish(context),500)),    com.irozon.sneaker.R.drawable.ic_error,R.string.unknown_error,R.color.white,R.color.red );
		}
	}

	public  void  onPhotoTaken(  byte[]  pictureBytes )
	{
		try
		{
			File  file = context.application().cache( -1,pictureBytes,2/* ChatContentType.IMAGE */ );

			this.takenMediaFiles.put(  MediaType.IMAGE.getValue(),      file );

			context.application().getMainLooperHandler().post( () -> { ObjectUtils.cast(context.findViewById(R.id.preview_switcher),ViewSwitcher.class).setDisplayedChild(1);context.findViewById(R.id.additional_switcher).setVisibility(View.GONE);  ObjectUtils.cast(context.findViewById(R.id.photo_viewer),PhotoDraweeView.class).setPhotoUri(Uri.fromFile(file));  ObjectUtils.cast(context.findViewById(R.id.control_switcher),ViewSwitcher.class).setDisplayedChild(1); } );
		}
		catch( Exception  e )
		{
			context.error(e);

            context.showSneakerWindow( Sneaker.with(context).setOnSneakerDismissListener(() -> context.application().getMainLooperHandler().postDelayed(() -> ContextUtils.finish(context),500)),    com.irozon.sneaker.R.drawable.ic_error,R.string.io_exception, R.color.white,R.color.red );
		}
	}
}
