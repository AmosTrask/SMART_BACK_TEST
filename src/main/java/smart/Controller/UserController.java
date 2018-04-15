package smart.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import smart.DTO.UserDto;
import smart.Entities.User;
import smart.Jwt.JwtTokenUtil;
import smart.Jwt.JwtUser;
import smart.Jwt.JwtUserFactory;
import smart.Repositories.UserRepository;
import smart.Services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;

@RestController
@CrossOrigin(origins = "http://localhost:8100" )
public class UserController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public JwtUser getAuthenticatedUser(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader).substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        return user;
    }


    @RequestMapping(path="/user/all", method = RequestMethod.GET)
    public Iterable<User> getAllUsers() throws ParseException {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }

    @RequestMapping(path="/user/add", method = RequestMethod.POST)
    public JwtUser addUser(@RequestBody @Valid UserDto userDto, HttpServletRequest request) {
        User user = userService.addUser(userDto);
        return JwtUserFactory.create(user);
    }
}
