package mixpowder.Translatebot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Main extends ListenerAdapter{

	private static String BotToken = "";
	private String ClientID = "";
	private String ClientSecret = "";

	public static void main(String[] args) {
		@SuppressWarnings("deprecation")
		JDABuilder jda = new JDABuilder();
		jda.addEventListeners(new Main());
		jda.setToken(BotToken);
		try {
			jda.build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	public void onMessageReceived(MessageReceivedEvent e){
		String s = e.getMessage().getContentRaw();
		if(!e.getAuthor().isBot()){
			if((s.contains("/translate") || s.contains("/trans") || s.contains("/翻訳")) && (s.contains("ja") || s.contains("en"))){
				String[] split = s.split(" ",3);
				if(split.length == 3){
				String target = split[1].equals("ja") ? "en" : "ja";
				String source = split[1];
				String message = split[2];
				String result = translateMessage(message,source,target);
				e.getChannel().sendMessage(result).complete();
				}else{
					e.getChannel().sendMessage("argument is wrong.\nhow to use: [/trans or /translate or /翻訳] [ja or en(choose what language you are using] [text] \nexample: /translate en hello").queue();
				}
			}else if(s.contains("/goodnight")){
				e.getChannel().sendMessage("yeah good night my sweet darling").complete();
				e.getJDA().shutdown();
			}else if(s.contains("/help translate")){
				e.getChannel().sendMessage("how to use: [/trans or /translate or /翻訳] [ja or en(choose what language you are using] [text] \nexample: /translate en hello\n※this translate is papago translate").queue();
			}
		}
	}

	public String translateMessage(String message, String source, String target){
		String string = "",line = "";
		try {
			URL objecturl = new URL("https://openapi.naver.com/v1/papago/n2mt");
			HttpURLConnection con = (HttpURLConnection)objecturl.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("X-Naver-Client-Id", ClientID);
			con.setRequestProperty("X-Naver-Client-Secret", ClientSecret);
			DataOutputStream output = new DataOutputStream(con.getOutputStream());
			output.writeBytes("text=" + URLEncoder.encode(message,"UTF-8") + "&target=" + target + "&source=" + source);
			output.flush();
			output.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			while((line = reader.readLine()) != null){
				string = string + line;
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		int a = string.indexOf("dText") + 8;
		int b = string .indexOf("engine");
		string = string.substring(a, b - 3);
		return string;
	}

}
