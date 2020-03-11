package brighton.ac.uk.ic257.swaphub;

public class UserProfile {

       String userName;
       String userSurname;
       String userCity;
       String uid;
       String FirebaseToken;

        public UserProfile(){
            // empty constructor
        }

        public UserProfile(String userName,String userSurname, String userCity){
            this.userName = userName;
            this.userSurname = userSurname;
            this.userCity = userCity;
        }
        public String getUserName() { return userName; }
        public String getUserSurname() {
            return userSurname;
        }
        public String getUserCity() {
            return userCity;
        }
}
