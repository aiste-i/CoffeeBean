<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <body>
    <h2>Change Email</h2>

    <c:if test="${not empty error}">
      <div style="color:red">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/business/change-email" method="post">
      <label>
        New email:
        <input type="email" name="email" required />
      </label><br/>

      <label>
        Confirm email:
        <input type="email" name="confirmEmail" required />
      </label><br/>

      <label>
        Current password:
        <input type="password" name="password" required />
      </label><br/>

      <button type="submit">Save</button>
    </form>
  </body>
</html>
