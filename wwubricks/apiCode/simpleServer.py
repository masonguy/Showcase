import ssl
from urllib.parse import urlparse
from urllib.parse import parse_qs
import http.server
import socketserver
from voteDBInterface import dbInterface

class MyHttpRequestHandler(http.server.SimpleHTTPRequestHandler):

    def do_GET(self):
        db = dbInterface()
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()
        retString = "bad request"
        queryParams = parse_qs(urlparse(self.path).query)

        if "action" in queryParams :
            action = queryParams["action"][0]
            if action == "dump":
                retString = str(db.dump())
            elif action == "getVote":
                if "postID" in queryParams:
                    postID = queryParams["postID"][0]
                    retString = str(db.getVote(postID))
                    retString = retString[1:len(retString)-1]
            elif action == "vote":
                postID = queryParams["postID"][0]
                voteType = queryParams["voteType"][0]
                db.vote(postID, voteType)
                retString = "voted"
            elif action == "createPost":
                db.createPost()
                retString = "created"

        self.wfile.write(bytes(retString, "utf8"))
        db.shutdown()
        return http.server.SimpleHTTPRequestHandler

    
    def do_POST(self):
        db = dbInterface()
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()
        response = "bad request"
        queryParams = parse_qs(urlparse(self.path).query)
        if "action" in queryParams:
            action = queryParams["action"][0]
            if action == "vote":
                postID = queryParams["postID"][0]
                voteType = queryParams["voteType"][0]
                db.vote(postID, voteType)
                response = "voted"
            elif action == "createPost":
                db.createPost()
                response = "created"

        self.wfile.write(bytes(response, "utf8"))
        db.shutdown()




def main():

    handlerObject = MyHttpRequestHandler
    PORT = 8000
    myServer = socketserver.TCPServer(("", PORT), handlerObject)
    myServer.serve_forever()

main()
