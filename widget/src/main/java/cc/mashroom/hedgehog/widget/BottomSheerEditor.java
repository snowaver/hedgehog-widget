package cc.mashroom.hedgehog.widget;

import  android.content.Context;
import  android.text.Editable;
import  android.text.TextWatcher;
import  android.view.View;
import  android.widget.EditText;

import  com.google.android.material.bottomsheet.BottomSheetDialog;

import  cc.mashroom.hedgehog.R;
import  cc.mashroom.util.ObjectUtils;
import  lombok.AccessLevel;
import  lombok.Getter;
import  lombok.Setter;
import  lombok.experimental.Accessors;

public  class  BottomSheerEditor  implements  View.OnClickListener,TextWatcher
{
    public  void  beforeTextChanged( CharSequence  text,int  start,int  count,int  after )
    {

    }

    public  void  onClick( View button )
    {
        String  editable = ObjectUtils.cast(this.bottomSheetDialog.findViewById(R.id.editor),EditText.class).getText().toString();

        ObjectUtils.cast(this.bottomSheetDialog.findViewById(R.id.editor),EditText.class).setText( "" );

        if( this.onEditCompleteListener !=  null )
        {
            this.onEditCompleteListener.onEditComplete( editable );
        }

        this.bottomSheetDialog.cancel();
    }

    public  void  afterTextChanged( Editable  textEditable )
    {

    }

    public  interface  OnEditCompleteListener{ public  void  onEditComplete(     CharSequence  text ); }

    public  BottomSheerEditor( Context  context , int  limitation )
    {
        this.setLimitation(limitation).setBottomSheetDialog(new  BottomSheetDialog(context,R.style.BottomSheetEditor)).getBottomSheetDialog().setContentView( R.layout.bottomsheet_editor );

        ObjectUtils.cast(this.bottomSheetDialog.findViewById(R.id.editor),EditText.class).addTextChangedListener( this );

        this.bottomSheetDialog.findViewById(R.id.cancel_button).setOnClickListener( (button) -> this.bottomSheetDialog.cancel() );

        this.bottomSheetDialog.findViewById(R.id.finish_button).setOnClickListener(this );
    }

    public  BottomSheerEditor  withText( CharSequence text )
    {
        ObjectUtils.cast(this.bottomSheetDialog.findViewById(R.id.editor),EditText.class).setText(text);

        ObjectUtils.cast(this.bottomSheetDialog.findViewById(R.id.editor),EditText.class).setSelection(  text.length() );

        return  this;
    }

    @Setter( value=AccessLevel.PRIVATE )
    @Getter
    @Accessors( chain = true )
    private  int   limitation;
    @Setter( value=AccessLevel.PRIVATE )
    @Getter
    @Accessors( chain = true )
    private  BottomSheetDialog  bottomSheetDialog;
    @Setter( value=AccessLevel.PUBLIC  )
    @Getter
    @Accessors( chain = true )
    private  OnEditCompleteListener  onEditCompleteListener;

    public  void    show()
    {
        this.bottomSheetDialog.show(  );
    }

    public  void  cancel()
    {
        this.bottomSheetDialog.cancel();
    }

    public  void  onTextChanged(     CharSequence  text,int  start,int before,int  count )
    {
        if( text.length() > limitation )
        {
            int  selectionStart = ObjectUtils.cast(this.bottomSheetDialog.findViewById(R.id.editor),EditText.class).getSelectionStart();

            ObjectUtils.cast(this.bottomSheetDialog.findViewById(R.id.editor),EditText.class).setText( new  StringBuilder(text).delete(start,start+count) );

            ObjectUtils.cast(this.bottomSheetDialog.findViewById(R.id.editor),EditText.class).setSelection( selectionStart  - 1 );
        }
    }
}