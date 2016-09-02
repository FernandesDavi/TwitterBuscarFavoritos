package br.edu.ifsp.btv.ads.pdmde16.twitterbuscarfavoritos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;


public class TwitterBuscasFavoritas extends ActionBarActivity {

    private EditText buscaEditText;
    private EditText salvarEditText;
    private Button botaoSalvar;
    private TableLayout tblBuscasTableLayout;
    private Button botaoLimpar;
    private SharedPreferences buscasSalvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_buscas_favoritas);
        // EditText BuscaEditText= (EditText) findViewById(R.id.buscarET);

        buscaEditText = (EditText) findViewById(R.id.buscarET);
        salvarEditText = (EditText) findViewById(R.id.SalvarEDT);
        botaoSalvar = (Button) findViewById(R.id.btnSalvar);
        tblBuscasTableLayout = (TableLayout)findViewById(R.id.BuscasTableLayout);
        botaoLimpar = (Button) findViewById(R.id.btnLimparTags);

        botaoSalvar.setOnClickListener(ouvidorBotaoSalvar);
        botaoLimpar.setOnClickListener(ouvidorBotaoLimparTags);
        escondeTeclado();
        buscasSalvas = getSharedPreferences("buscas", MODE_PRIVATE );
        //mode_private: esse aquivo � privado do app
        //MODE_WORLD_READER: o mundo inteiro pode ler o arquivo
        //MODE_WORLD_WRITABLE: o mundo inteiro pode ler
        //se precisar dos dois utilizar o operador binario entre eles "|"

        atualizarBotoes(null);
        //passar nulo para mostrar todas as tags depois de abrir e fechar


    }

   /* @Override
    protected void OnResume(){
        super.onResume();
        escondeTeclado();
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_twitter_buscas_favoritas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener ouvidorBotaoSalvar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (buscaEditText.getText().length() == 0 || salvarEditText.getText().length() == 0) {
                //exibir dialogo
                AlertDialog.Builder construtor = new AlertDialog.Builder(TwitterBuscasFavoritas.this);
                construtor.setTitle("Texto Faltando");
                construtor.setPositiveButton(android.R.string.ok, null);

                construtor.setMessage("Preencha os campos");

                AlertDialog dialogo = construtor.create();
                dialogo.show();
            }else{
                criarTag(buscaEditText.getText().toString(), salvarEditText.getText().toString());
                buscaEditText.setText("");
                salvarEditText.setText("");
                escondeTeclado();
            }
        }
    };

    private void escondeTeclado() {
        InputMethodManager gerenciadoEntrada = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        gerenciadoEntrada.hideSoftInputFromWindow(buscaEditText.getWindowToken(),0);
    }

    private void criarTag(String busca, String tag) {
        String buscaOriginal = buscasSalvas.getString(tag, null);
        SharedPreferences.Editor editor = buscasSalvas.edit();
        editor.putString(tag, busca);
        editor.apply();

        if (buscaOriginal == null){
            atualizarBotoes(tag);
        }
    }

    private void atualizarBotoes(String tag) {
        //1.busca todas as tags salvas
        String [] tags = buscasSalvas.getAll().keySet().toArray(new String[0]);

        //2.ordena essas tags de forma alfabetica
        Arrays.sort(tags, String.CASE_INSENSITIVE_ORDER);

        //3.cria os botoes para cada tag na interface
        //3.a se a tag for nova, eu apenas crio a tag nova
        if (tag != null){
            criarTagGUI(tag,Arrays.binarySearch(tags, tag));
        } else{
        //3.b se nao tiver a tag nova, eu recrio todas as tags
            for (int i =0; i< tags.length; i++ ){
                criarTagGUI(tags[i],i);

            }

        }
    }

    private void criarTagGUI(String tag, int indice) {
        LayoutInflater inflador = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View novaTag = inflador.inflate(R.layout.nova_tag, null);
        //novaTag.findViewById(R.id.btnBusca);
        Button botaoTag = (Button) novaTag.findViewById(R.id.btnBusca);
        botaoTag.setText(tag);
        botaoTag.setOnClickListener(ouvidorBotaoBusca);

        Button botaoEditar = (Button) novaTag.findViewById(R.id.btnEditar);
        botaoEditar.setOnClickListener(ouvidorBotaoEditar);
         tblBuscasTableLayout.addView(novaTag, indice);

    }

    private View.OnClickListener ouvidorBotaoLimparTags = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder construtor = new AlertDialog.Builder(TwitterBuscasFavoritas.this);
            construtor.setTitle(R.string.confirmacao);
            construtor.setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    limparTags();
                }

            });
            construtor.setCancelable(true);
            construtor.setNegativeButton(android.R.string.cancel, null);

            AlertDialog dialogo = construtor.create();
            dialogo.show();

        }
    };

    private void limparTags() {
        tblBuscasTableLayout.removeAllViews();

        SharedPreferences.Editor editor = buscasSalvas.edit();
        editor.clear();
        editor.apply();
    }

    private View.OnClickListener ouvidorBotaoBusca  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = ((Button) v).getText().toString();
            String busca = buscasSalvas.getString(tag, null);
            String url = getString(R.string.urlBusca) + busca;

            Intent abirURL = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            startActivity(abirURL);
        }
    };

    private View.OnClickListener ouvidorBotaoEditar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TableRow linhaPai = (TableRow) v.getParent();
            Button botaoTag = (Button) linhaPai.findViewById(R.id.btnBusca);

            String tag = botaoTag.getText().toString();
            String busca = buscasSalvas.getString(tag, null);
            buscaEditText.setText(busca);
            salvarEditText.setText(tag);
        }
    };


}
