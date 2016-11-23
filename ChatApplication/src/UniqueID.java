import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//gona be a static class. can't be multiple instances, thats why private constructor so we cant instansiate 
public class UniqueID {

	//amount of possible ID's to generate - 150 unique clients
	private static final int RANGE  = 1000;
	private static List<Integer> arrayID = new ArrayList<Integer>();
	private static int num = 0;
	

	
	//runs without any method, dont need to call it, this is why static
	static{	
		for(int i = 0; i < RANGE; i++){
			arrayID.add(i);
		}
		//shuffle ID's, random order
		Collections.shuffle(arrayID);
	}
	
	private UniqueID(){
		
	}
	
	//will never give same number thats why we increment it in loop
	public static int getID(){
		if(num > arrayID.size()) num = 0;
		//gets id at that index then incrememnts
		return arrayID.get(num++);
		
	}
}
