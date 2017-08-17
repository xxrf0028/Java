package handler;

public class HanlderHelp extends Handler {

	@Override
	public void doCmd(String word) {
		System.out.print("迷路了吗？你可以做的命令有：go bye help ");
        System.out.println("如：\t go east");
	}
	
}
