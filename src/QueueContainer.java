

import net.dv8tion.jda.core.entities.User;

public class QueueContainer {

	public String[] id;
	public User[] user;
	
	QueueContainer(){
		id = new String[5];
		user = new User[5];
	}
	QueueContainer(int i){
		id = new String[i];
		user = new User[i];
	}
	
	//METHODS
	
	void resetContainer(){
		id = new String[0];
		user = new User[0];
		id = new String[5];
		user = new User[5];
	}

	void resetContainer(int n){
		id = new String[0];
		user = new User[0];
		id = new String[n];
		user = new User[n];
	}
	
	void removeUser(int n){
		id[n] = null;
		user[n] = null;
		
		for(int i = 0; i < id.length - 1; i++){ // falling array
			if(id[i] == null){
				id[i] = id[i + 1];
				user[i] = user[i + 1];
				id[i + 1] = null;
				user[i + 1] = null;
			}
		}
	}
}
