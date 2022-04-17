package nerdsbattle.test;

public class Test2 {

  static int[][] board;

  
  public static void main(String[] args) {
    board = new int[100][100];
    line(50,50,70,90);

  }
  
  //http://tech-algorithm.com/articles/drawing-line-using-bresenham-algorithm/
  public static void line(int y, int x, int y2, int x2) {  
    int w = x2 - x ;
    int h = y2 - y ;
    
    int dx1 = w < 0 ? -1 : (w > 0 ? 1 : 0);
    int dy1 = h < 0 ? -1 : (h > 0 ? 1 : 0);
    int dx2 = w < 0 ? -1 : (w > 0 ? 1 : 0);
    int dy2 = 0;

    int longest = Math.abs(w) ;
    int shortest = Math.abs(h) ;
    if (!(longest>shortest)) {
        longest = Math.abs(h);
        shortest = Math.abs(w);
        dy2 = h < 0 ? -1 : (h > 0 ? 1 : dy2);
        dx2 = 0 ;            
    }
    int numerator = longest >> 1 ;
    for (int i=0;i<=longest;i++) {
        numerator += shortest ;
        if (!(numerator<longest)) {
            numerator -= longest ;
            x += dx1 ;
            y += dy1 ;
        } else {
            x += dx2 ;
            y += dy2 ;
        }
        board[y][x] = 1;
        printBoard();
    }
}
  
  public static void printBoard() {
    for(int r = 0; r < 100; r++) {
      for(int c = 0; c < 100; c++) {
        if(board[r][c] == 1) {
          System.out.print("#");
        }
        else {
          System.out.print(" ");
        }
      }
      System.out.println();
    }
    System.out.println();
  }

}
