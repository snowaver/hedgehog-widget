package cc.mashroom.hedgehog.widget;

import  android.app.Activity;
import  android.content.Context;
import  android.content.res.TypedArray;
import  android.graphics.Typeface;
import  android.graphics.drawable.ColorDrawable;
import  android.util.AttributeSet;
import  android.view.Gravity;
import  android.view.LayoutInflater;
import  android.view.View;
import  android.view.WindowManager;
import android.widget.ImageView;
import  android.widget.LinearLayout;
import  android.widget.RelativeLayout;
import  android.widget.TextView;

import  androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import  androidx.annotation.NonNull;
import  androidx.annotation.StringRes;

import  java.util.ArrayList;
import  java.util.List;

import  cc.mashroom.hedgehog.util.DensityUtils;
import  cc.mashroom.util.ObjectUtils;
import  cc.mashroom.hedgehog.R;
import  cc.mashroom.hedgehog.util.ContextUtils;
import  lombok.Getter;
import  lombok.Setter;
import  lombok.experimental.Accessors;

public  class  HeaderBar       extends  RelativeLayout  implements  View.OnClickListener
{
	public  HeaderBar  addDropdownItem( @StringRes  int  text,@ColorRes  int  textColor,float  textSize,Typeface  textTypeface,int  textGravity,@NonNull  LinearLayout.LayoutParams  layoutParams,int  leftPadding,int  topPadding,int  rightPadding,int  bottomPadding,int  dividerHeight )
	{
		if( this.addtionalDropdownContent.getChildCount()   >= 1 )
		{
			View  divider = new  View( super.getContext() );

			LinearLayout.LayoutParams  dividerLayoutParams = new  LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,dividerHeight );  dividerLayoutParams.setMargins( DensityUtils.px(super.getContext(),10),0,DensityUtils.px(super.getContext(),10),0 );

			divider.setLayoutParams(  dividerLayoutParams );

			divider.setBackgroundResource(    R.color.gainsboro );

			this.addtionalDropdownContent.addView(divider );
		}

		TextView  textview  = new  TextView( super.getContext() );

		textview.setLayoutParams( layoutParams );

		textview.setPadding(      leftPadding,topPadding, rightPadding, bottomPadding );

		textview.setText(    text );

		textview.setTextColor( super.getContext().getResources().getColor(textColor ) );

		textview.setTextSize(   textSize );

		textview.setTypeface(     textTypeface );

		textview.setGravity( textGravity );

		textview.setOnClickListener(this );          this.addtionalDropdownContent.addView( textview );

		return   this;
	}

	public  HeaderBar  addDropdownItem( @StringRes  int  text,@ColorRes  int  textColor,float  textSize,int  width,int  height )
	{
		return  addDropdownItem( text,textColor,textSize,Typeface.createFromAsset(super.getContext().getAssets(),"font/droid_sans_mono.ttf"),Gravity.RIGHT|Gravity.CENTER_VERTICAL,new  LinearLayout.LayoutParams(width,height),DensityUtils.px(super.getContext(),10),0,DensityUtils.px(super.getContext(),10),0,1 );
	}

	@Getter
	protected  LinearLayout  addtionalDropdownContent;

	public  List< View>  getDropdownItems()
	{
		List<View>  items    = new  ArrayList<View>();

		for( int  i = 0;i <= this.addtionalDropdownContent.getChildCount()-1;i = i + 1 )
		{
			if( i % 2 == 0 )
			{
				items.add( this.addtionalDropdownContent.getChildAt( i ) );
			}
		}

		return  items;
	}

    public  HeaderBar  setAdditionalText( String  additionalText )
    {
        ObjectUtils.cast(ObjectUtils.cast(super.findViewById(R.id.additional_switcher),ViewSwitcher.class).setDisplayedChild(0).getDisplayedChild(),TextView.class).setText(   additionalText );

        return   this;
    }

    public  HeaderBar  setAdditionalDrawable(    @DrawableRes  int  additionalDrawable )
    {
        ObjectUtils.cast(ObjectUtils.cast(super.findViewById(R.id.additional_switcher),ViewSwitcher.class).setDisplayedChild(1).getDisplayedChild(),ImageView.class).setImageResource( additionalDrawable );

        return   this;
    }

    public  HeaderBar  setBackText( String  backText )
    {
        ObjectUtils.cast(ObjectUtils.cast(super.findViewById(R.id.back_switcher),ViewSwitcher.class).setDisplayedChild(0).getDisplayedChild(),TextView.class).setText( backText );

        return   this;
    }

	public  HeaderBar  setBackDrawable(   @DrawableRes  int  backDrawable )
    {
        ObjectUtils.cast(ObjectUtils.cast(super.findViewById(R.id.back_switcher),ViewSwitcher.class).setDisplayedChild(1).getDisplayedChild(),ImageView.class).setImageResource( backDrawable );

        return   this;
    }

	public  interface  OnItemClickListener{public  void  onItemClick( View  itemView, int  position );}

	@Getter
	private TipWindow  dropdownMenu;
	@Accessors( chain=true )
	@Setter
	@Getter
	private OnItemClickListener   onItemClickListener;

	public  void  onClick( View  v )
	{
	    this.dropdownMenu.dismiss();

		if( this.onItemClickListener    != null )
		{
			this.onItemClickListener.onItemClick( v, this.addtionalDropdownContent.indexOfChild(v)/2 );
		}
	}

	public  void  setTitle( CharSequence  title )
	{
		ObjectUtils.cast(super.findViewById(R.id.title),TextView.class).setText(title );
	}

	public  HeaderBar( Context  context,AttributeSet  attributes )
	{
		super( context,attributes );

		LayoutInflater.from(context).inflate( R.layout.header_bar,  this );

		addtionalDropdownContent   = new  LinearLayout( context );

		addtionalDropdownContent.setLayoutParams( new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT) );
		/*
		addtionalDropdownContent.setBackgroundResource(    R.drawable.header_bar_dropdown_background );
		*/
		addtionalDropdownContent.setBackground( new  ColorDrawable(ObjectUtils.cast(super.getBackground(),ColorDrawable.class).getColor()) );

		addtionalDropdownContent.setOrientation(   LinearLayout.VERTICAL );

		this.dropdownMenu= new  TipWindow( context,this.addtionalDropdownContent,true );

		ObjectUtils.cast(super.findViewById(R.id.additional_switcher),ViewSwitcher.class).setOnClickListener( (addtionalText) -> dropdownMenu.showAsDropDown(this,context.getResources().getDisplayMetrics().widthPixels,1) );

		TypedArray  typedArray    = context.obtainStyledAttributes( attributes,R.styleable.HeaderBar );

		if( typedArray.getBoolean(R.styleable.HeaderBar_immersive, false) )
		{
			ObjectUtils.cast(context,Activity.class).getWindow().addFlags(    WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );

			ColorDrawable  color = new  ColorDrawable( ObjectUtils.cast(super.getBackground(),ColorDrawable.class).getColor() );

			color.setAlpha(   128 );

			ObjectUtils.cast(context,Activity.class).getWindow().setStatusBarColor( color.getColor() );
		}

		if( typedArray.hasValue(R.styleable.HeaderBar_backDrawable      ) )
		{
			ObjectUtils.cast(ObjectUtils.cast(super.findViewById(R.id.back_switcher),ViewSwitcher.class).setDisplayedChild(1).getDisplayedChild(),ImageView.class).setImageDrawable(             typedArray.getDrawable(R.styleable.HeaderBar_backDrawable) );
		}

		if( typedArray.hasValue(R.styleable.HeaderBar_additionalDrawable) )
		{
			ObjectUtils.cast(ObjectUtils.cast(super.findViewById(R.id.additional_switcher),ViewSwitcher.class).setDisplayedChild(1).getDisplayedChild(),ImageView.class).setImageDrawable( typedArray.getDrawable(R.styleable.HeaderBar_additionalDrawable) );
		}

		ObjectUtils.cast(super.findViewById(R.id.back_text),TextView.class).setText(  !typedArray.hasValue(R.styleable.HeaderBar_backText) ? "" : typedArray.getString(R.styleable.HeaderBar_backText) );

		ObjectUtils.cast(super.findViewById(R.id.title),TextView.class).setText( !typedArray.hasValue(R.styleable.HeaderBar_android_title) ? "" : typedArray.getString(R.styleable.HeaderBar_android_title) );

		ObjectUtils.cast(super.findViewById(R.id.back_switcher),ViewSwitcher.class).setOnClickListener( ( v ) -> ContextUtils.finish(ObjectUtils.cast(context)) );

		ObjectUtils.cast(super.findViewById(R.id.additional_text),TextView.class).setText(     !typedArray.hasValue(R.styleable.HeaderBar_additionalText) ? "" : typedArray.getString(R.styleable.HeaderBar_additionalText) );  typedArray.recycle();
	}
}
