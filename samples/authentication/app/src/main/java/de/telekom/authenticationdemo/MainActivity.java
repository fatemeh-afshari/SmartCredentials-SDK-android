package de.telekom.authenticationdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.telekom.smartcredentials.authentication.AuthStateManager;
import de.telekom.smartcredentials.authentication.factory.SmartCredentialsAuthenticationFactory;
import de.telekom.smartcredentials.core.api.AuthenticationApi;
import de.telekom.smartcredentials.core.authentication.AuthenticationError;
import de.telekom.smartcredentials.core.authentication.AuthenticationTokenResponse;
import de.telekom.smartcredentials.core.authentication.AuthorizationException;
import de.telekom.smartcredentials.core.authentication.OnFreshTokensRetrievedListener;
import de.telekom.smartcredentials.core.authentication.TokenRefreshListener;
import de.telekom.smartcredentials.core.responses.SmartCredentialsApiResponse;

public class MainActivity extends AppCompatActivity {

    private AuthenticationApi authenticationApi;
    private TokensAdapter adapter;
    private List<Token> tokens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authenticationApi = SmartCredentialsAuthenticationFactory.getAuthenticationApi();

        RecyclerView recyclerView = findViewById(R.id.tokens_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        tokens = new ArrayList<>();
        adapter = new TokensAdapter(this, tokens);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchTokens();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_profile_item:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.perform_action_item:
                new Thread(() -> authenticationApi.performActionWithFreshTokens(new OnFreshTokensRetrievedListener() {
                    @Override
                    public void onRefreshComplete(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException exception) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, R.string.perform_action_success,
                                    Toast.LENGTH_SHORT).show();
                            fetchTokens();
                        });
                    }

                    @Override
                    public void onFailed(AuthenticationError errorDescription) {
                        runOnUiThread(() -> runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                R.string.perform_action_failed, Toast.LENGTH_SHORT).show()));
                    }
                })).start();
                return true;
            case R.id.refresh_access_item:
                new Thread(() -> authenticationApi.refreshAccessToken(new TokenRefreshListener<AuthenticationTokenResponse>() {
                    @Override
                    public void onTokenRequestCompleted(@Nullable AuthenticationTokenResponse response, @Nullable AuthorizationException exception) {
                        runOnUiThread(() -> {
                            fetchTokens();
                            Toast.makeText(MainActivity.this, R.string.refresh_access_token_success,
                                    Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailed(AuthenticationError errorDescription) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                R.string.refresh_access_token_failed, Toast.LENGTH_SHORT).show());
                    }
                })).start();
                return true;
            case R.id.logout_item:
                LogoutDialogFragment logoutDialogFragment = new LogoutDialogFragment();
                logoutDialogFragment.show(getSupportFragmentManager(), LogoutDialogFragment.TAG);
                new Thread(() -> {
                    SmartCredentialsApiResponse<Boolean> response = authenticationApi.logOut();
                    runOnUiThread(() -> {
                        logoutDialogFragment.dismiss();
                        if (response.isSuccessful() && response.getData()) {
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, R.string.logout_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchTokens() {
        new Thread(() -> {
            AuthStateManager authStateManager = AuthStateManager.getInstance(this, IdentityProvider.GOOGLE.getName());
            List<Token> tokenList = new ArrayList<>();
            tokenList.add(new Token(R.string.access_token, authStateManager.getAccessToken(), authStateManager.getAccessTokenExpirationTime()));
            tokenList.add(new Token(R.string.id_token, authStateManager.getIdToken(), Token.DEFAULT_VALIDITY));
            tokenList.add(new Token(R.string.refresh_token, authStateManager.getRefreshToken(), Token.DEFAULT_VALIDITY));
            tokens.clear();
            tokens.addAll(tokenList);
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }).start();

    }
}