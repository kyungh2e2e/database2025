package DB2025Team07;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/JoinMemberServlet")
public class DB2025Team07_JoinMemberServlet extends HttpServlet {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/DB2025Team07?serverTimezone=UTC";
    static final String USER = "DB2025Team07";
    static final String PASS = "DB2025Team07";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String id = request.getParameter("user_id");
        String pwd = request.getParameter("pwd");
        String nickname = request.getParameter("nickname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            conn.setAutoCommit(false); // 🔸 트랜잭션 시작

            String sql = "INSERT INTO DB2025_Users (id, pwd, nickname, email, phone) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, pwd);
            pstmt.setString(3, nickname);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);

            int result = pstmt.executeUpdate();

            if (result > 0) {
                conn.commit(); // 🔸 성공 시 커밋
                HttpSession session = request.getSession();
                session.setAttribute("user_id", id);
                session.setAttribute("nickname", nickname);
                response.sendRedirect("joinsuccess.jsp");
            } else {
                conn.rollback(); // 🔸 실패 시 롤백
            }

        } catch (SQLIntegrityConstraintViolationException dup) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('❗ 중복된 학번/이메일/전화번호입니다.');");
            out.println("history.back();"); // ← 폼으로 자동 복귀
            out.println("</script>");          
            
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            e.printStackTrace();
            response.getWriter().println("DB 오류: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // 🔸 다시 자동 커밋 모드로 전환
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
